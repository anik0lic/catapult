package raf.rma.catapult.quiz.ui

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rma.catapult.cats.repository.CatsRepository
import raf.rma.catapult.leaderboard.api.model.LeaderboardPost
import raf.rma.catapult.leaderboard.repository.LeaderboardRepository
import raf.rma.catapult.photos.repository.PhotoRepository
import raf.rma.catapult.profile.datastore.ProfileDataStore
import raf.rma.catapult.quiz.db.QuizResult
import raf.rma.catapult.quiz.ui.QuizContract.QuizState
import raf.rma.catapult.quiz.model.QuestionType
import raf.rma.catapult.quiz.model.QuizQuestion
import raf.rma.catapult.quiz.repository.QuizRepository
import raf.rma.catapult.quiz.ui.QuizContract.QuizEvent
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val photoRepository: PhotoRepository,
    private val catsRepository: CatsRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()
    private fun setState(reducer: QuizState.() -> QuizState) = _state.update(reducer)

    private val events = MutableSharedFlow<QuizEvent>()
    fun setEvent(event: QuizEvent) { viewModelScope.launch { events.emit(event) } }

    private var pausedTimeRemaining: Long = 0
    private var timer: CountDownTimer? = null

    init {
        observeEvents()
        getQuestions()
    }

    private fun getQuestions(){
        viewModelScope.launch {
            setState { copy(loading = true) }
            try{
                withContext(Dispatchers.IO){
                    val questions = generateQuestions()
                    setState { copy(questions = questions, loading = false) }
                }
                startTimer()
            } catch (error: Exception){
                Log.d("QuizViewModel", "Exception", error)
                setState { copy(loading = false, error = error) }
            }
            finally {
                Log.d("QUIZ", "Questions generated: ${state.value.questions}")
            }
        }
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is QuizEvent.StopQuiz -> {
                        timer?.cancel()
                        setState { copy(showExitDialog = true) }
                    }
                    is QuizEvent.ContinueQuiz -> {
                        pausedTimeRemaining = state.value.timeRemaining
                        startTimer(pausedTimeRemaining)
                        setState { copy(showExitDialog = false) }
                    }
                    is QuizEvent.FinishQuiz -> finishQuiz()
                    is QuizEvent.OptionSelected -> submitAnswer(state.value.questions[state.value.currentQuestionIndex].incorrectAnswers[event.optionIndex])
                    is QuizEvent.PublishScore -> publish()
                }
            }
        }
    }

    private fun startTimer(time: Long = 300000) {
        timer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setState { copy(timeRemaining = millisUntilFinished) }
            }

            override fun onFinish() {
                setState { copy(questions = emptyList()) }
                setState { copy(quizFinished = true)}
                val score = calculateScore().coerceAtMost(100.00f)
                setState { copy(score = score) }
            }
        }
        timer?.start()
    }

    fun submitAnswer(option: String){
        viewModelScope.launch {
            val currentQuestion = state.value.questions[state.value.currentQuestionIndex]
            val correctAnswer = currentQuestion.correctAnswer
            setState { copy(selectedOption = option)}
            val isCorrect = option == correctAnswer

            if (isCorrect) {
                setState {
                    copy(
                        correctAnswers = state.value.correctAnswers + 1,
                    )
                }
            }

            delay(1000)

            if (_state.value.currentQuestionIndex < state.value.questions.size - 1) {
                setState {
                    copy(
                        currentQuestionIndex = currentQuestionIndex + 1,
                        selectedOption = null
                    )
                }
            }
            else {
                setState { copy(questions = emptyList()) }
                setState { copy(quizFinished = true)}
                val score = calculateScore().coerceAtMost(100.00f)
                setState { copy(score = score) }
                timer?.cancel()
            }
        }
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            Log.d("QUIZ", "Quiz completed with score: ${state.value.score}")

            val userProfile = profileDataStore.data.first()

            val quizResult = QuizResult(
                nickname = userProfile.nickname,
                score = state.value.score,
                date = Date().toString(),
            )
            quizRepository.insertQuizResult(quizResult)
        }
    }

    private fun publish(){
        viewModelScope.launch {
            Log.d("QUIZ", "Quiz completed with score: ${state.value.score}")

            val userProfile = profileDataStore.data.first()

            val leaderboardPost = LeaderboardPost(
                nickname = userProfile.nickname,
                result = state.value.score,
                category = 1
            )
            val response = leaderboardRepository.postLeaderboard(leaderboardPost)

            val quizResult = QuizResult(
                nickname = userProfile.nickname,
                score = state.value.score,
                date = Date().toString(),
                ranking = response.ranking
            )
            quizRepository.insertQuizResult(quizResult)
        }
    }

    private fun calculateScore(): Float {
        Log.d("QUIZ", "Correct answers: ${state.value.correctAnswers}, Time remaining: ${state.value.timeRemaining}")

        return (state.value.correctAnswers * 2.5 * (1 + ((state.value.timeRemaining / 1000) + 120) / 300)).toFloat()
    }

    private suspend fun generateQuestions(): List<QuizQuestion>{
        val cats = catsRepository.getAllCats()
        val questionCats = cats.shuffled().take(23)
        val questions = mutableListOf<QuizQuestion>()

        Log.d("QUIZ", "Question cats: $questionCats")

        questionCats.forEach { cat ->
            var photos = photoRepository.getAllPhotos().filter { it.catId == cat.id }.shuffled()

            if(photos.isEmpty()){
                try {
                    photoRepository.fetchPhoto(cat.referenceImageId, cat.id)
                } catch (error: Exception){
                    Log.d("QUIZ", "Error fetching photo", error)
                }
            }

            photos = photoRepository.getAllPhotos().filter { it.catId == cat.id }.shuffled()
            Log.d("QUIZ", "Question Photos: $photos")

            when ((1..3).random()) {
                1 -> if(photos.isNotEmpty()){
                    val incorrectAnswers = cats.filter { it.id != cat.id }
                    .shuffled()
                    .map { it.name }
                    .distinct()
                    .take(3)

                    questions.add(
                        QuizQuestion(
                            question = "What breed is the cat in the picture?",
                            correctAnswer = cat.name,
                            incorrectAnswers = incorrectAnswers,
                            imageUrl = photos.first().url,
                            questionType = QuestionType.CAT_NAME,
                            options = (incorrectAnswers + cat.name).shuffled()
                        )
                    )
                }

                2 -> if(photos.isNotEmpty()){
                    val incorrectAnswers = cats.filter { it.origin != cat.origin }
                        .shuffled()
                        .map { it.origin }
                        .distinct()
                        .take(3)

                    questions.add(
                        QuizQuestion(
                            question = "Where is the cat in the picture from?",
                            correctAnswer = cat.origin,
                            incorrectAnswers = incorrectAnswers,
                            imageUrl = photos.first().url,
                            questionType = QuestionType.CAT_ORIGIN,
                            options = (incorrectAnswers + cat.origin).shuffled()
                        )
                    )
                }

                3 -> if(photos.isNotEmpty()){
                    val incorrectAnswers =
                        cats.filter { it.temperament != cat.temperament }
                            .shuffled()
                            .map { it.temperament }
                            .distinct()
                            .take(3)

                    questions.add(
                        QuizQuestion(
                            question = "What is the temperament of the cat in the picture?",
                            correctAnswer = cat.temperament,
                            incorrectAnswers = incorrectAnswers,
                            imageUrl = photos.first().url,
                            questionType = QuestionType.CAT_TEMPERAMENT,
                            options = (incorrectAnswers + cat.temperament).shuffled()
                        )
                    )
                }
            }
        }
        Log.d("QUIZ", "Questions generated: $questions")
        return questions.take(20)
    }
}
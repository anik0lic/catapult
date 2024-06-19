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
        startTimer()
    }

    private fun getQuestions(){
        viewModelScope.launch {
            setState { copy(loading = true) }
            try{
                withContext(Dispatchers.IO){
                    val questions = generateQuestions()
                    setState { copy(questions = questions, loading = false) }
                }
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
                    is QuizEvent.NextQuestion -> {}
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
                }
            }
        }
    }

    private fun startTimer(time: Long = 300000) {
        timer = object : CountDownTimer(time, 1000) { // 1000ms = 1s, 300000ms = 5min
            override fun onTick(millisUntilFinished: Long) {
                setState { copy(timeRemaining = millisUntilFinished) }
            }

            override fun onFinish() {
//                // ukoliko nije zavrsio sva pitanja a vreme je isteklo, prelazimo na Result
//                setState { copy(questions = emptyList()) } // vracamo na Result screen
//
//                val ubp = calculateUBP();
//                ubp.coerceAtMost(maximumValue = 100.00f)
//                setState { copy(ubp = ubp) }
                finishQuiz()

            }
        }
        timer?.start() // timer? means that timer can be null
    }

    fun submitAnswer(option: String){
        viewModelScope.launch {
            val currentQuestion = state.value.questions[state.value.currentQuestionIndex]
            val correctAnswer = currentQuestion.correctAnswer
            val isCorrect = option == correctAnswer
            if (isCorrect) {
                setState {
                    copy(
                        correctAnswers = state.value.correctAnswers + 1,
                        selectedOption = option
                    )
                }
            }

            delay(1000)

            if (_state.value.currentQuestionIndex < state.value.questions.size - 1) {
                setState {
                    copy(
                        currentQuestionIndex = currentQuestionIndex + 1,
                        selectedOption = null
//                    isOptionCorrect = null
                    )
                }
            } else {
                finishQuiz()
            }
        }
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            Log.d("QUIZ", "Quiz completed with score: ${state.value.score}")
            setState { copy(questions = emptyList()) }
            setState { copy(quizFinished = true) }

            val userProfile = profileDataStore.data.first()
            val score = calculateScore().coerceAtMost(100.00f)

            val quizResult = QuizResult(
                nickname = userProfile.nickname,
                score = score,
                date = Date().toString(), // trenutni datum
            )
            quizRepository.insertQuizResult(quizResult)

            setState { copy(score = score) }
        }
    }

    private fun calculateScore(): Float {
        return (state.value.score * 2.5 * (1 + ((state.value.timeRemaining / 1000) + 120) / 300)).toFloat()
    }

    private suspend fun generateQuestions(): List<QuizQuestion>{
        val cats = catsRepository.getAllCats()
        val questionCats = cats.shuffled().take(20)
        val questions = mutableListOf<QuizQuestion>()
        Log.d("QUIZ", "Question cats: $questionCats")
        questionCats.forEach { cat ->
            val photos = photoRepository.getAllPhotos().filter { it.catId == cat.id }.shuffled()
            Log.d("QUIZ", "Question Photos: $photos")
            when ((1..3).random()) {
                1 -> if (photos.isNotEmpty()) {
                    val incorrectAnswers = cats.filter { it.id != cat.id }
                        .shuffled()
                        .take(3)
                        .map { it.name }

                    questions.add(
                        QuizQuestion(
                            question = "Koja je rasa mačke sa slike?",
                            correctAnswer = cat.name,
                            incorrectAnswers = incorrectAnswers,
                            imageUrl = photos.first().url,
                            questionType = QuestionType.CAT_NAME,
                            options = (incorrectAnswers + cat.name).shuffled()
                        )
                    )
                }

                2 -> if (photos.isNotEmpty()) {
                    val incorrectAnswers = cats.filter { it.origin != cat.origin }
                        .shuffled()
                        .take(3)
                        .map { it.origin }

                    questions.add(
                        QuizQuestion(
                            question = "Odakle je mačka sa slike?",
                            correctAnswer = cat.origin,
                            incorrectAnswers = incorrectAnswers,
                            imageUrl = photos.first().url,
                            questionType = QuestionType.CAT_ORIGIN,
                            options = (incorrectAnswers + cat.origin).shuffled()
                        )
                    )
                }

                3 -> if (photos.isNotEmpty()) {
                    val incorrectAnswers =
                        cats.filter { it.temperament != cat.temperament }
                            .shuffled()
                            .take(3)
                            .map { it.temperament }

                    questions.add(
                        QuizQuestion(
                            question = "Kakav je karakter mačke sa slike?",
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
        return questions
    }
}
package raf.rma.catapult.quiz.ui//package raf.rma.catapult.quiz

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.rememberImagePainter
import raf.rma.catapult.quiz.model.QuizQuestion
import raf.rma.catapult.quiz.ui.QuizContract.QuizEvent
import raf.rma.catapult.quiz.ui.QuizContract.QuizState

fun NavGraphBuilder.quiz(
    route: String,
    onQuizCompleted: () -> Unit,
    onClose: () -> Unit,
    onPublishScore: () -> Unit,
) = composable(
    route = route,
//    arguments = arguments,
) { navBackStackEntry ->

    val quizViewModel: QuizViewModel = hiltViewModel(navBackStackEntry)

    val state = quizViewModel.state.collectAsState()

    QuizScreen(
        state = state.value,
        eventPublisher = {
            quizViewModel.setEvent(it)
        },
        onOptionSelected = { option -> quizViewModel.submitAnswer(option) },
        onQuizCompleted = onQuizCompleted,
        onClose = onClose,
        onPublishScore = onPublishScore
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    state: QuizState,
    eventPublisher: (uiEvent: QuizEvent) -> Unit,
    onOptionSelected: (String) -> Unit,
    onQuizCompleted: () -> Unit,
    onClose: () -> Unit,
    onPublishScore: () -> Unit
) {
    if (state.showExitDialog) {
        AlertDialog(
            onDismissRequest = { eventPublisher(QuizEvent.StopQuiz) },
            title = { Text("Exit Quiz") },
            text = { Text("Are you sure you want to exit the quiz?") },
            confirmButton = {
                Button(
                    onClick = { onClose() }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { eventPublisher(QuizEvent.ContinueQuiz) }
                ) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Quiz") },
                navigationIcon = {
                    IconButton(onClick = { eventPublisher(QuizEvent.StopQuiz) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },actions = {
                    Text(
                        text = "Time remaining: ${state.timeRemaining / 1000} sec",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.loading) {
                    CircularProgressIndicator()
                } else if (state.questions.isNotEmpty()) {
                    val question = state.questions[state.currentQuestionIndex]
                    QuestionScreen(
                        question = question,
                        onOptionSelected = onOptionSelected
                    )
                } else if (state.quizFinished){
                    ResultScreen(
                        score = state.score,
                        onFinish = onClose,
                        onPublish = onPublishScore
                    )
                } else {
                    onQuizCompleted()
                    Log.d("QUIZ", "No questions available")
                }
            }
        }
    )
}

@Composable
fun QuestionScreen(
    question: QuizQuestion,
    onOptionSelected: (String) -> Unit
) {
    val quizViewModel: QuizViewModel = hiltViewModel()
    val state = quizViewModel.state.collectAsState().value

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (question.imageUrl != null) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = rememberImagePainter(question.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val options = question.options
            options.forEach { option ->
                val buttonColor = when {
                    state.selectedOption == null -> MaterialTheme.colorScheme.primary
                    (option == state.selectedOption) && (option == state.questions[state.currentQuestionIndex].correctAnswer) -> Color.Green
                    (option == state.selectedOption) && (option != state.questions[state.currentQuestionIndex].correctAnswer) -> Color.Red
                    else -> MaterialTheme.colorScheme.primary
                }
                Button(
                    onClick = { if (state.selectedOption == null) onOptionSelected(option) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(option)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    score: Float,
    onFinish: () -> Unit,
    onPublish: () -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Quiz Result") }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Your Score: $score",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = onPublish,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Publish")
                        }
                        Button(
                            onClick = onFinish,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Finish")
                        }
                    }
                }
            }
        }
    )
}
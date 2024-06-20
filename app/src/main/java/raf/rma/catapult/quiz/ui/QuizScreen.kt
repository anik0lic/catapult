package raf.rma.catapult.quiz.ui//package raf.rma.catapult.quiz

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.rememberAsyncImagePainter
import raf.rma.catapult.core.theme.Orange
import raf.rma.catapult.quiz.model.QuizQuestion
import raf.rma.catapult.quiz.ui.QuizContract.QuizEvent
import raf.rma.catapult.quiz.ui.QuizContract.QuizState

fun NavGraphBuilder.quiz(
    route: String,
    onQuizCompleted: () -> Unit,
    onClose: () -> Unit
) = composable(
    route = route,
    enterTransition = { slideInVertically { it } },
    popExitTransition = { slideOutVertically { it } },
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
        onClose = onClose
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    state: QuizState,
    eventPublisher: (uiEvent: QuizEvent) -> Unit,
    onOptionSelected: (String) -> Unit,
    onQuizCompleted: () -> Unit,
    onClose: () -> Unit
) {
    if (state.showExitDialog) {
        AlertDialog(
            onDismissRequest = { eventPublisher(QuizEvent.StopQuiz) },
            title = { Text("Exit Quiz") },
            text = { Text("Are you sure you want to exit the quiz?") },
            confirmButton = {
                Button(
                    onClick = { onClose() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { eventPublisher(QuizEvent.ContinueQuiz) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quiz",
                        style = TextStyle(
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { eventPublisher(QuizEvent.StopQuiz) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        text = "Time remaining: ${state.timeRemaining / 1000 / 60} min ${state.timeRemaining / 1000 % 60} sec",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                if (state.loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.questions.isNotEmpty()) {
                    Crossfade(targetState = state.currentQuestionIndex, label = "") { index ->
                        val question = state.questions[index]
                        QuestionScreen(
                            question = question,
                            onOptionSelected = onOptionSelected
                        )
                    }
                } else if (state.quizFinished){
                    ResultScreen(
                        score = state.score,
                        onFinish = onClose,
                        eventPublisher = { eventPublisher(it) }
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
        BackHandler {
            quizViewModel.setEvent(QuizEvent.StopQuiz)
        }

        Text(
            text = question.question,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        if (question.imageUrl != null) {
            Box(
                modifier = Modifier
                    .width(350.dp)
                    .height(280.dp)
                    .border(2.dp, Orange)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(question.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color.Transparent, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
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
                    state.selectedOption == null -> MaterialTheme.colorScheme.surface
                    option == state.selectedOption && option == state.questions[state.currentQuestionIndex].correctAnswer -> Color.Green
                    option == state.selectedOption && option != state.questions[state.currentQuestionIndex].correctAnswer -> Color.Red
                    option != state.selectedOption && option == state.questions[state.currentQuestionIndex].correctAnswer -> Color.Green
                    else -> MaterialTheme.colorScheme.surface
                }
                Button(
                    onClick = {
                        if (state.selectedOption == null) onOptionSelected(option)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(2.dp, Orange)
                ) {
                    Text(
                        text = option,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ResultScreen(
    score: Float,
    onFinish: () -> Unit,
    eventPublisher: (uiEvent: QuizEvent) -> Unit,
) {
    Scaffold(
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(bottom = 100.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Your Score: $score",
                        style = TextStyle(
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                        modifier = Modifier.padding(bottom = 30.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                eventPublisher(QuizEvent.PublishScore(score))
                                onFinish()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Orange,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "Publish",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                ),
                            )
                        }
                        Button(
                            onClick = {
                                eventPublisher(QuizEvent.FinishQuiz)
                                onFinish()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Orange,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "Finish",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                ),
                            )
                        }
                    }
                }
            }
        }
    )
}
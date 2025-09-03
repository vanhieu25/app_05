package com.example.app_05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_05.ui.theme.App_05Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_05Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Calculator(modifier: Modifier = Modifier) {
    var expression by remember { mutableStateOf("") }
    var display by remember { mutableStateOf("0") }
    var previousValue by remember { mutableStateOf(0.0) }
    var operation by remember { mutableStateOf("") }
    var waitingForOperand by remember { mutableStateOf(false) }

    fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
    }

    fun inputNumber(number: String) {
        if (waitingForOperand) {
            display = number
            expression = expression + " " + number
            waitingForOperand = false
        } else {
            display = if (display == "0") number else display + number
            if (expression.isEmpty() || expression.endsWith(" ")) {
                expression = expression + number
            } else {
                // Cập nhật số cuối cùng trong biểu thức
                val parts = expression.split(" ")
                val newParts = parts.dropLast(1) + display
                expression = newParts.joinToString(" ")
            }
        }
    }

    fun inputOperation(nextOperation: String) {
        val inputValue = display.toDoubleOrNull() ?: 0.0

        if (operation.isNotEmpty() && !waitingForOperand) {
            val result = when (operation) {
                "+" -> previousValue + inputValue
                "-" -> previousValue - inputValue
                "×" -> previousValue * inputValue
                "÷" -> if (inputValue != 0.0) previousValue / inputValue else 0.0
                else -> inputValue
            }
            display = formatNumber(result)
            expression = formatNumber(result) + " " + nextOperation
            previousValue = result
        } else {
            previousValue = inputValue
            if (expression.isEmpty()) {
                expression = display + " " + nextOperation
            } else if (!expression.endsWith(" ")) {
                expression = expression + " " + nextOperation
            } else {
                // Thay thế phép toán cuối
                val parts = expression.split(" ")
                expression = parts.dropLast(1).joinToString(" ") + " " + nextOperation
            }
        }

        waitingForOperand = true
        operation = nextOperation
    }

    fun calculate() {
        val inputValue = display.toDoubleOrNull() ?: 0.0
        
        if (operation.isNotEmpty() && !waitingForOperand) {
            val result = when (operation) {
                "+" -> previousValue + inputValue
                "-" -> previousValue - inputValue
                "×" -> previousValue * inputValue
                "÷" -> if (inputValue != 0.0) previousValue / inputValue else 0.0
                else -> inputValue
            }
            display = formatNumber(result)
            expression = expression + " = " + formatNumber(result)
            previousValue = 0.0
            operation = ""
            waitingForOperand = true
        }
    }

    fun clear() {
        expression = ""
        display = "0"
        previousValue = 0.0
        operation = ""
        waitingForOperand = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Hiển thị biểu thức
                Text(
                    text = if (expression.isEmpty()) "" else expression,
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
                
                // Hiển thị số hiện tại
                Text(
                    text = display,
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.End
                )
            }
        }

        // Buttons
        val buttonModifier = Modifier
            .aspectRatio(1f)
            .weight(1f)

        // First row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "C",
                onClick = { clear() },
                modifier = buttonModifier,
                backgroundColor = Color.Gray
            )
            CalculatorButton(
                text = "±",
                onClick = { 
                    if (display != "0") {
                        display = if (display.startsWith("-")) {
                            display.substring(1)
                        } else {
                            "-$display"
                        }
                        // Cập nhật biểu thức với số đã đổi dấu
                        if (expression.isNotEmpty() && !expression.endsWith(" ")) {
                            val parts = expression.split(" ")
                            val newParts = parts.dropLast(1) + display
                            expression = newParts.joinToString(" ")
                        }
                    }
                },
                modifier = buttonModifier,
                backgroundColor = Color.Gray
            )
            CalculatorButton(
                text = "%",
                onClick = { 
                    val value = display.toDoubleOrNull() ?: 0.0
                    val result = value / 100
                    display = formatNumber(result)
                    // Cập nhật biểu thức
                    if (expression.isNotEmpty() && !expression.endsWith(" ")) {
                        val parts = expression.split(" ")
                        val newParts = parts.dropLast(1) + display
                        expression = newParts.joinToString(" ")
                    } else {
                        expression = display
                    }
                },
                modifier = buttonModifier,
                backgroundColor = Color.Gray
            )
            CalculatorButton(
                text = "÷",
                onClick = { inputOperation("÷") },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }

        // Second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "7",
                onClick = { inputNumber("7") },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "8",
                onClick = { inputNumber("8") },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "9",
                onClick = { inputNumber("9") },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "×",
                onClick = { inputOperation("×") },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }

        // Third row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "4",
                onClick = { inputNumber("4") },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "5",
                onClick = { inputNumber("5") },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "6",
                onClick = { inputNumber("6") },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "-",
                onClick = { inputOperation("-") },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }

        // Fourth row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "1",
                onClick = { inputNumber("1") },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "2",
                onClick = { inputNumber("2") },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "3",
                onClick = { inputNumber("3") },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "+",
                onClick = { inputOperation("+") },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }

        // Fifth row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                text = "0",
                onClick = { inputNumber("0") },
                modifier = Modifier
                    .aspectRatio(2f)
                    .weight(2f)
            )
            CalculatorButton(
                text = ".",
                onClick = { 
                    if (!display.contains(".")) {
                        display += "."
                        // Cập nhật biểu thức
                        if (expression.isNotEmpty() && !expression.endsWith(" ")) {
                            val parts = expression.split(" ")
                            val newParts = parts.dropLast(1) + display
                            expression = newParts.joinToString(" ")
                        } else {
                            expression = display
                        }
                    }
                },
                modifier = buttonModifier
            )
            CalculatorButton(
                text = "=",
                onClick = { calculate() },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF333333)
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    App_05Theme {
        Calculator()
    }
}
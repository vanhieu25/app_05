package com.example.app_05

// Import các thư viện Android cần thiết
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

// Import các thư viện Jetpack Compose cho UI
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

// Import theme tùy chỉnh của ứng dụng
import com.example.app_05.ui.theme.App_05Theme

/**
 * MainActivity - Lớp chính của ứng dụng máy tính
 * Kế thừa từ ComponentActivity để hỗ trợ Jetpack Compose
 */
class MainActivity : ComponentActivity() {
    /**
     * Phương thức onCreate được gọi khi Activity được khởi tạo
     * Thiết lập giao diện người dùng và cấu hình cơ bản
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Cho phép hiển thị full màn hình
        setContent {
            App_05Theme { // Áp dụng theme tùy chỉnh
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/**
 * Calculator - Composable chính hiển thị giao diện máy tính
 * @param modifier Modifier để tùy chỉnh layout và appearance
 */
@Composable
fun Calculator(modifier: Modifier = Modifier) {
    // Các state variables quản lý trạng thái của máy tính
    var expression by remember { mutableStateOf("") }        // Biểu thức toán học được hiển thị
    var display by remember { mutableStateOf("0") }          // Giá trị hiện tại trên màn hình
    var previousValue by remember { mutableStateOf(0.0) }    // Giá trị trước đó để tính toán
    var operation by remember { mutableStateOf("") }         // Phép toán hiện tại (+, -, ×, ÷)
    var waitingForOperand by remember { mutableStateOf(false) } // Đang chờ số mới hay không

    /**
     * Định dạng số hiển thị - loại bỏ phần thập phân nếu là số nguyên
     * @param value Giá trị số cần định dạng
     * @return Chuỗi đã được định dạng
     */
    fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
    }

    /**
     * Xử lý khi người dùng nhập số
     * @param number Chuỗi số được nhập vào (0-9)
     */
    fun inputNumber(number: String) {
        if (waitingForOperand) {
            // Nếu đang chờ số mới, thay thế display và thêm vào biểu thức
            display = number
            expression = expression + " " + number
            waitingForOperand = false
        } else {
            // Nếu đang nhập tiếp, nối số vào display hiện tại
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

    /**
     * Xử lý khi người dùng chọn phép toán (+, -, ×, ÷)
     * @param nextOperation Phép toán được chọn
     */
    fun inputOperation(nextOperation: String) {
        val inputValue = display.toDoubleOrNull() ?: 0.0

        if (operation.isNotEmpty() && !waitingForOperand) {
            // Nếu có phép toán đang chờ và đã có số mới, thực hiện tính toán
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
            // Lưu giá trị hiện tại và thiết lập phép toán mới
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

    /**
     * Thực hiện phép tính khi người dùng nhấn nút "="
     * Hiển thị kết quả cuối cùng và reset trạng thái
     */
    fun calculate() {
        val inputValue = display.toDoubleOrNull() ?: 0.0
        
        if (operation.isNotEmpty() && !waitingForOperand) {
            // Thực hiện phép tính dựa trên phép toán đã chọn
            val result = when (operation) {
                "+" -> previousValue + inputValue
                "-" -> previousValue - inputValue
                "×" -> previousValue * inputValue
                "÷" -> if (inputValue != 0.0) previousValue / inputValue else 0.0
                else -> inputValue
            }
            display = formatNumber(result)
            expression = expression + " = " + formatNumber(result)
            
            // Reset trạng thái sau khi tính toán
            previousValue = 0.0
            operation = ""
            waitingForOperand = true
        }
    }

    /**
     * Xóa tất cả dữ liệu và reset máy tính về trạng thái ban đầu
     * Được gọi khi người dùng nhấn nút "C" (Clear)
     */
    fun clear() {
        expression = ""
        display = "0"
        previousValue = 0.0
        operation = ""
        waitingForOperand = false
    }

    // Layout chính của máy tính với background đen
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Khu vực hiển thị (Display Area)
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
                // Hiển thị biểu thức toán học (phía trên)
                Text(
                    text = if (expression.isEmpty()) "" else expression,
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
                
                // Hiển thị số hiện tại (phía dưới, lớn hơn)
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

        // Khu vực các nút bấm (Button Area)
        val buttonModifier = Modifier
            .aspectRatio(1f)  // Tỷ lệ 1:1 để tạo nút vuông
            .weight(1f)       // Phân bổ đều không gian

        // Hàng thứ nhất: C, ±, %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Nút Clear - xóa tất cả
            CalculatorButton(
                text = "C",
                onClick = { clear() },
                modifier = buttonModifier,
                backgroundColor = Color.Gray
            )
            // Nút đổi dấu +/-
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
            // Nút phần trăm
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
            // Nút chia
            CalculatorButton(
                text = "÷",
                onClick = { inputOperation("÷") },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }

        // Hàng thứ hai: 7, 8, 9, ×
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
            // Nút nhân
            CalculatorButton(
                text = "×",
                onClick = { inputOperation("×") },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }

        // Hàng thứ ba: 4, 5, 6, -
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
            // Nút trừ
            CalculatorButton(
                text = "-",
                onClick = { inputOperation("-") },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }

        // Hàng thứ tư: 1, 2, 3, +
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
            // Nút cộng
            CalculatorButton(
                text = "+",
                onClick = { inputOperation("+") },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }

        // Hàng thứ năm: 0 (rộng gấp đôi), ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Nút số 0 có kích thước gấp đôi
            CalculatorButton(
                text = "0",
                onClick = { inputNumber("0") },
                modifier = Modifier
                    .aspectRatio(2f)  // Tỷ lệ 2:1 để rộng gấp đôi
                    .weight(2f)       // Chiếm 2 phần không gian
            )
            // Nút dấu thập phân
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
            // Nút bằng để thực hiện phép tính
            CalculatorButton(
                text = "=",
                onClick = { calculate() },
                modifier = buttonModifier,
                backgroundColor = Color(0xFFFF9500)
            )
        }
    }
}

/**
 * CalculatorButton - Composable tạo nút bấm cho máy tính
 * @param text Văn bản hiển thị trên nút
 * @param onClick Hàm callback khi nút được nhấn
 * @param modifier Modifier để tùy chỉnh layout
 * @param backgroundColor Màu nền của nút (mặc định là xám đậm)
 */
@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF333333) // Màu xám đậm mặc định
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp), // Bo góc 20dp
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,              // Kích thước chữ lớn
            fontWeight = FontWeight.Bold,   // Chữ đậm
            color = Color.White             // Màu chữ trắng
        )
    }
}

/**
 * CalculatorPreview - Preview cho giao diện máy tính
 * Sử dụng để xem trước giao diện trong Android Studio Design view
 */
@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    App_05Theme {
        Calculator()
    }
}
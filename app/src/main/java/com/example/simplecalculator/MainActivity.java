package com.example.simplecalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText display;
    Button btnC, btnEq, btnBackspace;
    Button[] numButtons = new Button[10]; // for buttons 0-9
    Button btnAdd, btnSub, btnMul, btnDiv;
    String currentExpression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        btnC = findViewById(R.id.btn_c);
        btnEq = findViewById(R.id.btn_eq);
        btnBackspace = findViewById(R.id.btn_backspace);
        btnAdd = findViewById(R.id.btn_add);
        btnSub = findViewById(R.id.btn_sub);
        btnMul = findViewById(R.id.btn_mul);
        btnDiv = findViewById(R.id.btn_div);

        // Set up number buttons (0-9)
        for (int i = 0; i <= 9; i++) {
            String buttonID = "btn_" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            numButtons[i] = findViewById(resID);
            numButtons[i].setOnClickListener(this);
        }

        // Set up operator buttons
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnMul.setOnClickListener(this);
        btnDiv.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnEq.setOnClickListener(this);

        // Backspace button functionality
        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = display.getText().toString();
                if (currentText.length() > 0) {
                    // Remove the last character
                    currentText = currentText.substring(0, currentText.length() - 1);
                    display.setText(currentText);
                    currentExpression = currentText; // Update the current expression as well
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        // Handle number button clicks
        for (int i = 0; i <= 9; i++) {
            if (id == numButtons[i].getId()) {
                currentExpression += i;
                display.setText(currentExpression);
                return;
            }
        }

        // Handle operator button clicks
        if (id == R.id.btn_add) {
            currentExpression += "+";
        } else if (id == R.id.btn_sub) {
            currentExpression += "-";
        } else if (id == R.id.btn_mul) {
            currentExpression += "*";
        } else if (id == R.id.btn_div) {
            currentExpression += "/";
        }

        // Clear button
        if (id == R.id.btn_c) {
            currentExpression = "";
            display.setText("");
        }

        // Equals button
        if (id == R.id.btn_eq) {
            try {
                double result = evaluate(currentExpression);
                display.setText(String.valueOf(result));
                currentExpression = String.valueOf(result); // Update the expression for further calculations
            } catch (Exception e) {
                display.setText("Error");
                currentExpression = ""; // Reset the expression on error
            }
        } else {
            display.setText(currentExpression);
        }
    }

    // Simple expression evaluator
    private double evaluate(String expression) {
        // Replace division symbol for evaluation
        expression = expression.replace("÷", "/");
        return new ExpressionEvaluator().evaluate(expression);
    }

    // Inner class to evaluate mathematical expressions
    class ExpressionEvaluator {
        public double evaluate(String expression) {
            // Use stacks for operators and operands
            Stack<Double> values = new Stack<>();
            Stack<Character> operators = new Stack<>();

            for (int i = 0; i < expression.length(); i++) {
                char ch = expression.charAt(i);

                // If the character is a number, push it to the stack
                if (Character.isDigit(ch)) {
                    StringBuilder sb = new StringBuilder();
                    while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                        sb.append(expression.charAt(i++));
                    }
                    i--;
                    values.push(Double.parseDouble(sb.toString()));
                } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                    while (!operators.isEmpty() && hasPrecedence(ch, operators.peek())) {
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                    }
                    operators.push(ch);
                }
            }

            // Apply remaining operators
            while (!operators.isEmpty()) {
                values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
            }

            return values.pop();
        }

        private boolean hasPrecedence(char op1, char op2) {
            if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
                return false;
            } else {
                return true;
            }
        }

        private double applyOperator(char op, double b, double a) {
            switch (op) {
                case '+': return a + b;
                case '-': return a - b;
                case '*': return a * b;
                case '/': if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                    return a / b;
            }
            return 0;
        }
    }
}

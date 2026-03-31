package com.example.my_scientific_calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private String currentText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvDisplay = findViewById(R.id.tvDisplay);
    }

    public void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        if (buttonText.equals("C")) {
            // Clears the entire display
            currentText = "";
            tvDisplay.setText("0");
        } else if (buttonText.equals("DEL")) {
            if (currentText.length() > 0) {
                currentText = currentText.substring(0, currentText.length() - 1);
                tvDisplay.setText(currentText.isEmpty() ? "0" : currentText);
            }
        } else if (buttonText.equals("=")) {
            try {
                String expression = currentText.replace("×", "*").replace("÷", "/").replace("π", "3.14159265359").replace("e", "2.71828182845");

                double result = evaluateMath(expression);

                String resultString = String.valueOf(result);
                if (resultString.endsWith(".0")) {
                    resultString = resultString.replace(".0", "");
                }

                tvDisplay.setText(resultString);
                currentText = resultString;
            } catch (Exception e) {
                tvDisplay.setText("Error");
                currentText = "";
            }
        } else if (buttonText.equals("SIN") || buttonText.equals("COS") || buttonText.equals("TAN") ||
                buttonText.equals("LOG") || buttonText.equals("LN")) {
            currentText = currentText + buttonText.toLowerCase() + "(";
            tvDisplay.setText(currentText);
        } else if (buttonText.equals("SIN⁻¹")) {
            currentText = currentText + "asin(";
            tvDisplay.setText(currentText);
        } else if (buttonText.equals("COS⁻¹")) {
            currentText = currentText + "acos(";
            tvDisplay.setText(currentText);
        } else if (buttonText.equals("TAN⁻¹")) {
            currentText = currentText + "atan(";
            tvDisplay.setText(currentText);
        } else if (buttonText.equals("√")) {
            currentText = currentText + "sqrt(";
            tvDisplay.setText(currentText);
        } else if (buttonText.equals("X²")) {
            currentText = currentText + "^2";
            tvDisplay.setText(currentText);
        } else if (buttonText.equals("Xʸ") || buttonText.equals("^")) {
            currentText = currentText + "^";
            tvDisplay.setText(currentText);
        } else if (buttonText.equals("1/X")) {
            currentText = currentText + "1/(";
            tvDisplay.setText(currentText);
        } else if (buttonText.equals("N!") || buttonText.equals("!")) {
            currentText = currentText + "factorial(";
            tvDisplay.setText(currentText);
        } else {
            currentText = currentText + buttonText;
            tvDisplay.setText(currentText);
        }
    }

    // --- MATH PARSER (Locked to Degrees) ---
    public static double evaluateMath(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                    if (eat('%')) x = x / 100.0;
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();

                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else if (func.equals("asin")) x = Math.toDegrees(Math.asin(x));
                    else if (func.equals("acos")) x = Math.toDegrees(Math.acos(x));
                    else if (func.equals("atan")) x = Math.toDegrees(Math.atan(x));
                    else if (func.equals("log")) x = Math.log10(x);
                    else if (func.equals("ln")) x = Math.log(x);
                    else if (func.equals("factorial")) x = (double)factorial((int)x);
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    public static long factorial(int n) {
        if (n < 0) return 0;
        if (n == 0 || n == 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
package com.example.calculator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {


    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btn_point, btn_AC, btn_delete, btn_AddSub, btn_div, btn_mult, btn_sub, btn_add, btn_percentage, btn_equleto;
    EditText display_screen;
    TextView reslut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }




        display_screen = findViewById(R.id.display_screen);
        display_screen.setShowSoftInputOnFocus(false);

        btn1 = findViewById(R.id.btn_for_1);
        btn2 = findViewById(R.id.btn_for_2);
        btn3 = findViewById(R.id.btn_for_3);
        btn4 = findViewById(R.id.btn_for_4);
        btn5 = findViewById(R.id.btn_for_5);
        btn6 = findViewById(R.id.btn_for_6);
        btn7 = findViewById(R.id.btn_for_7);
        btn8 = findViewById(R.id.btn_for_8);
        btn9 = findViewById(R.id.btn_for_9);
        btn0 = findViewById(R.id.btn_for_0);
        btn_point = findViewById(R.id.btn_for_point);
        btn_AC = findViewById(R.id.btn_for_AC);
        btn_delete = findViewById(R.id.btn_for_delete);
        btn_equleto = findViewById(R.id.btn_for_equleto);
        btn_AddSub = findViewById(R.id.btn_for_AddSub);
        btn_add = findViewById(R.id.btn_for_add);
        btn_sub = findViewById(R.id.btn_for_sub);
        btn_mult = findViewById(R.id.btn_for_mult);
        btn_div = findViewById(R.id.btn_for_div);
        btn_percentage = findViewById(R.id.btn_for_percentage);
        reslut = findViewById(R.id.result);

        View.OnClickListener listener = view -> {
            Button btn = (Button) view;
            String currentText = display_screen.getText().toString();
            String appendText = btn.getText().toString();

            if (appendText.equals(".")) {
                if (currentText.isEmpty() || currentText.endsWith(".")) {
                    return;
                }

                int i = currentText.length() - 1;
                while (i >= 0 && (Character.isDigit(currentText.charAt(i)) || currentText.charAt(i) == '.')) {
                    if (currentText.charAt(i) == '.') {
                        return;
                    }
                    i--;
                }
            }


            if ("+-*/".contains(appendText)) {
                if (currentText.isEmpty()) return;
                char lastChar = currentText.charAt(currentText.length() - 1);
                if ("+-*/".indexOf(lastChar) != -1) {
                    // Replace last operator with new one
                    display_screen.setText(currentText.substring(0, currentText.length() - 1) + appendText);
                    display_screen.setSelection(display_screen.getText().length());
                    return;
                }
            }

            display_screen.append(appendText);
        };

        btn0.setOnClickListener(listener);
        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        btn4.setOnClickListener(listener);
        btn5.setOnClickListener(listener);
        btn6.setOnClickListener(listener);
        btn7.setOnClickListener(listener);
        btn8.setOnClickListener(listener);
        btn9.setOnClickListener(listener);
        btn_point.setOnClickListener(listener);
        btn_add.setOnClickListener(listener);
        btn_sub.setOnClickListener(listener);
        btn_mult.setOnClickListener(listener);
        btn_div.setOnClickListener(listener);

        btn_percentage.setOnClickListener(view -> {
            String current = display_screen.getText().toString();
            if (!current.isEmpty() && Character.isDigit(current.charAt(current.length() - 1))) {
                display_screen.append("/100");
            }
        });

        btn_AddSub.setOnClickListener(view -> {
            String current = display_screen.getText().toString();
            if (!current.isEmpty() && !current.startsWith("-")) {
                display_screen.setText("-" + current);
            } else if (current.startsWith("-")) {
                display_screen.setText(current.substring(1));
            }
        });

        btn_equleto.setOnClickListener(view -> {
            String input = display_screen.getText().toString();
            if (!input.isEmpty()) {
                try {
                    double result = evaluateExpression(input);
                    if (Double.isNaN(result) || Double.isInfinite(result)) {
                        reslut.setText("Error");
                    } else {
                        reslut.setText("= " + String.format("%.2f", result));
                    }
                    reslut.setVisibility(VISIBLE);
                } catch (Exception e) {
                    reslut.setText("Error");
                    reslut.setVisibility(VISIBLE);
                }
            }
        });

        btn_AC.setOnClickListener(view -> {
            display_screen.setText("");
            reslut.setVisibility(GONE);
        });

        btn_delete.setOnClickListener(view -> {
            String res = display_screen.getText().toString();
            if (!res.isEmpty()) {
                display_screen.setText(res.substring(0, res.length() - 1));
            }
        });
    }


    public static double evaluateExpression(String expression) {
        try {
            expression = expression.replaceAll("\\s+", "");
            List<String> postfix = infixToPostfix(expression);
            if (postfix.isEmpty()) return Double.NaN;
            return evaluatePostfix(postfix);
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    public static List<String> infixToPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Stack<Character> stack = new Stack<>();
        StringBuilder number = new StringBuilder();
        boolean expectUnary = true;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                number.append(c);
                expectUnary = false;
            } else {
                if (number.length() > 0) {
                    output.add(number.toString());
                    number.setLength(0);
                }

                if (c == '(') {
                    stack.push(c);
                    expectUnary = true;
                } else if (c == ')') {
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        output.add(String.valueOf(stack.pop()));
                    }
                    if (!stack.isEmpty()) stack.pop(); // pop '('
                    expectUnary = false;
                } else {
                    // Unary minus detection
                    if (c == '-' && expectUnary) {
                        number.append('-');
                        expectUnary = false;
                    } else {
                        while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c)) {
                            output.add(String.valueOf(stack.pop()));
                        }
                        stack.push(c);
                        expectUnary = true;
                    }
                }
            }
        }

        if (number.length() > 0) output.add(number.toString());
        while (!stack.isEmpty()) output.add(String.valueOf(stack.pop()));

        return output;
    }


    public static double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else {
                if (stack.size() < 2) return Double.NaN;
                double b = stack.pop();
                double a = stack.pop();

                switch (token) {
                    case "+":
                        stack.push(a + b);
                        break;
                    case "-":
                        stack.push(a - b);
                        break;
                    case "*":
                        stack.push(a * b);
                        break;
                    case "/":
                        if (b == 0) return Double.NaN;
                        stack.push(a / b);
                        break;
                    default:
                        return Double.NaN;
                }
            }
        }

        if (stack.size() != 1) return Double.NaN;
        return stack.pop();
    }

    public static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }
}

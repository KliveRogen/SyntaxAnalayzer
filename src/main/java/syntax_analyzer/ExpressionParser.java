package syntax_analyzer;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParser {
    /*
    Правила анализатора:

        expr: plusminus* EOF ;

        plusminus: multdiv ( ( '+' | '-') multdiv )* ;

        multdiv: factor (('*' | '/') factor)* ;

        factor: NUMBER | '(' expr ')' ;
        //////////////////


    */
    public static void main(String[] args) {
        String expressionText = "12 + 122 * 6 - 12 + (12 * 21 / 12)";
        List<Lexeme> lexemes = lexAnalyze(expressionText);
        System.out.println(lexemes);
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        System.out.println(expr(lexemeBuffer));

    }

    public static List<Lexeme> lexAnalyze(String expText) {
        List<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    continue;
                default:
                    if (c >= '0' && c <= '9') {
                        StringBuilder stringBuilder = new StringBuilder();
                        do {
                            stringBuilder.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c >= '0' && c <= '9');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, stringBuilder.toString()));
                    } else {
                        if(c != ' '){
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF,""));
        return lexemes;
    }

    //factor: NUMBER | '(' expr ')' ;
    public static int factor(LexemeBuffer lexemes){
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type){
            case NUMBER:
                return Integer.parseInt(lexeme.value);
            case LEFT_BRACKET:
                int value = expr(lexemes);
                lexeme = lexemes.next();
                if(lexeme.type != LexemeType.RIGHT_BRACKET){
                    throw new RuntimeException("Unexpected token: " + lexeme.value + " at position: " + lexemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value + " at position: " + lexemes.getPos());
        }
    }

    public static int plusminus(LexemeBuffer lexemes){
        int value  = muldiv(lexemes);
        while(true){
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type){
                case OP_PLUS:
                    value += muldiv(lexemes);
                    break;
                case OP_MINUS:
                    value -= muldiv(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    //multdiv: factor (('*' | '/') factor)* ;
    public static int muldiv(LexemeBuffer lexemes){
        int value  = factor(lexemes);
        while(true){
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type){
                case OP_MUL:
                    value *= factor(lexemes);
                    break;
                case OP_DIV:
                    value /= factor(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    //expr: plusminus* EOF ;
    public static int expr(LexemeBuffer lexemes){
        Lexeme lexeme = lexemes.next();
        if(lexeme.type == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }


    /*
    Правила анализатора:

        expr: plusminus* EOF ;

        plusminus: multdiv ( ( '+' | '-') multdiv )* ;

        multdiv: factor (('*' | '/') factor)* ;

        factor: NUMBER | '(' expr ')' ;
    */
}
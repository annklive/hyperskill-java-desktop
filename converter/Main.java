package converter;

import java.util.Scanner;

public class Main {

    public static final int MODE_EXIT = 0;
    public static final int MODE_FROM_DECIMAL = 1;
    public static final int MODE_TO_DECIMAL = 2;
    
    public static String convertFromDecimal(int number, int base) {
        StringBuffer sbuf = new StringBuffer();
        while (number != 0) {
            int rem = number % base;
            number = number / base;
            if (rem <= 9) {
                sbuf.append(Integer.toString(rem));
            } else {
                // hexa decimal 
                char c = (char)((int) 'A' + (rem - 10));
                sbuf.append(c);
            }
        }
        return sbuf.reverse().toString();
    }

    public static int selectMode(Scanner scanner) {
        while (true) {
            System.out.print(
                "Do you want to convert /from decimal or /to decimal? (To quit type /exit) ");
            String option = scanner.next();
            if (option.equals("/exit")) {
                return MODE_EXIT;
            } else if (option.equals("/from")) {
                return MODE_FROM_DECIMAL;
            } else if (option.equals("/to")) {
                return MODE_TO_DECIMAL;
            }
        }
    }

    public static void doConvertFromDecimal(Scanner scanner) {
        System.out.print("Enter number in decimal system: ");
        int number = scanner.nextInt();
        System.out.print("Enter target base: ");
        int base = scanner.nextInt();
        String cNum = convertFromDecimal(number, base);
        System.out.println("Conversion result: " + cNum);
    }

    public static int convertToDecimal(String number, int base) {
        int d = 0;

        int power = number.length() - 1;
        for (int i = 0; i < number.length(); i++) {
            char n = Character.toUpperCase(number.charAt(i));
            int num;
            if (base == 16 && n >= 'A' && n <= 'F') {
                num = 10 + (n - 'A'); 
            } else {
                num = (n - '0'); 
            }
            d += num * Math.pow(base, power);
            power--;
        }
        return d;
    }

    public static void doConvertToDecimal(Scanner scanner) {
        System.out.print("Enter source number: ");
        String number = scanner.next();
        System.out.print("Enter source base: ");
        int base = scanner.nextInt();
        int decimal = convertToDecimal(number, base);
        System.out.println("Conversion to decimal result: " + decimal);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int mode = selectMode(scanner);
            if (mode == MODE_EXIT) {
                break;
            }
            if (mode == MODE_FROM_DECIMAL) {
                doConvertFromDecimal(scanner);
            } else if (mode == MODE_TO_DECIMAL) {
                doConvertToDecimal(scanner);
            }
            System.out.println();
        }
    }
}


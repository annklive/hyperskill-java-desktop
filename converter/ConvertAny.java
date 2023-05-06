package converter;

import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigInteger;

public class ConvertAny {

    public static BigInteger intToBigInt(int n) {
        return new BigInteger(Integer.toString(n));
    }

    public static BigInteger longToBigInt(long l) {
        return new BigInteger(Long.toString(l));
    }

    public static BigInteger convertToDecimal(String number, int base) {
        BigInteger d  = BigInteger.ZERO;

        int power = number.length() - 1;
        for (int i = 0; i < number.length(); i++) {
            char n = Character.toUpperCase(number.charAt(i));
            BigInteger num;
            if (base > 10 && (n >= 'A' && n <= 'Z')) {
                num = new BigInteger("10").add(intToBigInt(n - 'A'));
            } else {
                num = intToBigInt(n - '0');
            }
            d = d.add(num.multiply(longToBigInt((long)Math.pow(base, power))));
            power--;
        }
        return d;
    }

    public static String convertFromDecimal(BigInteger number, int base) {
        StringBuffer sbuf = new StringBuffer();
        while (number != BigInteger.ZERO) {
            BigInteger rem = number.mod(intToBigInt(base));
            number = number.divide(intToBigInt(base));

            if (rem.compareTo(new BigInteger("9")) == 1) {
                char c = (char)((int) 'A' + (rem.intValue() - 10));
                sbuf.append(c);
            } else {
                sbuf.append(rem.toString());
            }
        }
        return sbuf.reverse().toString();
    }

    public static List<Integer> getBases(Scanner scanner) {
        while (true) {
            System.out.print(
                "Enter two numbers in format: {source base} {target base} (To quit type /exit) ");
            String input = scanner.nextLine();
            if (input.equals("/exit")) {
                return null;
            } 
            try {
                
                List<Integer> bases =  
                    Arrays.asList(input.split(" "))
                        .stream()
                        .map(Integer::parseInt)
                        .filter((base) -> {
                            return base >= 2 && base <= 36;
                        })
                        .collect(Collectors.toList());
                if (bases.size() == 2) {
                    return bases;
                } 
            } catch (Exception e) {
                
            }
       }
    }

    public static String getSourceNumber(Scanner scanner, 
        int sourceBase, int targetBase) {
        System.out.printf(
            "Enter number in base %d to convert to base %d (To go back type /back) ", sourceBase, targetBase);
        return scanner.nextLine();
    }

    public static String doConvert(String number, 
        int sourceBase, int targetBase) {

        // convert the source base to decimal
        BigInteger decimalNumber;
        if (sourceBase != 10) {
            decimalNumber = convertToDecimal(number, sourceBase);    
        } else {
            decimalNumber = new BigInteger(number);
        }
        // convert decimal to target
        if (targetBase != 10) {
            return convertFromDecimal(decimalNumber, targetBase);
        } else {
            return decimalNumber.toString();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // first level 
            List<Integer> bases = getBases(scanner);
            if (bases == null) {
                break;
            }
            int sourceBase = bases.get(0);
            int targetBase = bases.get(1);

            while (true) {
                // second level
                String srcNumber = getSourceNumber(scanner, sourceBase, targetBase);
                if (srcNumber.equals("/back")) {
                    break;
                } 
                String result = doConvert(srcNumber, sourceBase, targetBase);
                System.out.println("Conversion result: " + result + "\n");
            }
        }
    }
}

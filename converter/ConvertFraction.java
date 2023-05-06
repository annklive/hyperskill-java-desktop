package converter;

import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConvertFraction {
    
    static final int outputScale = 5;

    public static BigInteger intToBigInt(int n) {
        return new BigInteger(Integer.toString(n));
    }

    public static BigDecimal longToBigDecimal(long l) {
        return new BigDecimal(Long.toString(l));
    }

    public static BigDecimal intToBigDecimal(int n) {
        return new BigDecimal(Integer.toString(n));
    }

    public static BigDecimal charToBigDecimal(char n, int base) {
        n = Character.toLowerCase(n);
        BigDecimal num;
        if (base > 10 && (n >= 'a' && n <= 'z')) {
            num = new BigDecimal("10").add(intToBigDecimal(n - 'a'));
        } else {
            num = intToBigDecimal(n - '0');
        }
        return num;
    }

    public static BigDecimal convertNumberToDecimal(String number, int base) {
        BigDecimal d  = BigDecimal.ZERO;

        int power = number.length() - 1;
        for (int i = 0; i < number.length(); i++) {
            BigDecimal num = charToBigDecimal(number.charAt(i), base);
            d = d.add(num.multiply(longToBigDecimal((long)Math.pow(base, power))));
            power--;
        }
        return d;
    }

    public static BigDecimal convertFractionToDecimal(String fraction, int base)
    {
        BigDecimal f = BigDecimal.ZERO;

        for (int p = 0; p < fraction.length(); p++) {
            BigDecimal num = charToBigDecimal(fraction.charAt(p), base);
            int power = (p + 1) * -1;
            f = f.add(num.multiply(
                new BigDecimal(Double.toString(Math.pow(base, power)))));
        }

        return f;
    }


    public static BigDecimal convertToDecimal(String number, int base) {
        String[] parts = number.split("\\.");
        BigDecimal wholePart, decimalPart;

        if (parts.length >= 2) {
            wholePart = convertNumberToDecimal(parts[0], base);
            decimalPart = convertFractionToDecimal(parts[1], base);
        } else {
            wholePart = convertNumberToDecimal(number, base);
            decimalPart = BigDecimal.ZERO;
        }

        return decimalPart.add(wholePart);
    }

    public static String convertNumberFromDecimal(BigInteger number,
    BigInteger base) {
        StringBuffer sbuf = new StringBuffer();

        while (number != BigInteger.ZERO) {
            BigInteger rem = number.mod(base);
            number = number.divide(base);

            if (rem.compareTo(BigInteger.TEN) >= 0) {
                char c = (char)((int) 'a' + (rem.intValue() - 10));
                sbuf.append(c);
            } else {
                sbuf.append(rem.toString());
            }
        }
        return sbuf.reverse().toString();
    }

    public static String convertFractionFromDecimal(BigDecimal f, BigDecimal base)
    {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(".");
        BigDecimal number = f;

        int numFraction = 0;
        while (numFraction < outputScale && number.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal m = number.multiply(base).setScale(outputScale, RoundingMode.HALF_UP);
            BigDecimal n = m.setScale(0, RoundingMode.FLOOR);
            if (n.compareTo(BigDecimal.TEN) >= 0) {
                char c = (char)((int) 'a' + (n.intValue() - 10));
                sbuf.append(c);
            } else {
                sbuf.append(n.toString());
            }
            numFraction++;
            number = m.subtract(n);
        }
        if (sbuf.length() < outputScale) {
            while (sbuf.length() < outputScale+1) {
                sbuf.append("0");
            }
        }
        return sbuf.toString();
    }

    public static String convertFromDecimal(BigDecimal number, int base, int scale) {
        BigInteger iBase = intToBigInt(base);

        BigDecimal wholePart = number.setScale(0,RoundingMode.FLOOR);
        BigDecimal fractionPart = number.subtract(wholePart);

        String whole = convertNumberFromDecimal(
                            new BigInteger(wholePart.toString()), iBase); 
        BigDecimal dBase = intToBigDecimal(base);
        String fraction = convertFractionFromDecimal(fractionPart, dBase);

        return whole + fraction;
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
        int sourceBase, int targetBase, int scale) {

        // convert the source base to decimal
        BigDecimal decimalNumber;
        if (sourceBase != 10) {
            decimalNumber = convertToDecimal(number, sourceBase);    
        } else {
            decimalNumber = new BigDecimal(number);
        }

        // convert decimal to target
        if (targetBase != 10) {
            return convertFromDecimal(decimalNumber, targetBase, scale);
        } else {
            return decimalNumber.setScale(scale, RoundingMode.FLOOR).toString();
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
                    System.out.println();
                    break;
                } 
                int scale = outputScale;
                if (srcNumber.indexOf(".") == -1) {
                    scale = 0;
                }
                String result = doConvert(srcNumber, sourceBase, targetBase, scale);
                if (srcNumber.indexOf(".") == -1) {
                    result = result.substring(0, result.indexOf("."));
                }
                System.out.println("Conversion result: " + result + "\n");
            }
        }
    }
}

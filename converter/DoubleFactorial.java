package converter;

import java.math.BigInteger;
import java.util.Scanner;

class DoubleFactorial {
    public static BigInteger calcDoubleFactorial(int n) {        
        if (n == 0) {
            return BigInteger.ONE;
        }
        BigInteger result = new BigInteger(String.valueOf(n));
        while (n-2 > 0) {
            result = result.multiply(new BigInteger(String.valueOf(n-2)));
            n -= 2;
        }
        return result;        
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        System.out.println(calcDoubleFactorial(n));
    }
}


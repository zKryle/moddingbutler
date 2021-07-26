package moddingbutler.util;

import java.util.Arrays;

public class MathUtil {
	static <T> T[] append(T[] arr, T element) {
		final int N = arr.length;
		arr = Arrays.copyOf(arr, N + 1);
		arr[N] = element;
		return arr;
	}
}

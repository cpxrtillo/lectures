import java.io.IOException;
import java.nio.file.Path;

public class EliteFileConverter {

	public static char toLeetSpeak(char letter) {
		boolean random = Math.random() < 0.5;

		switch (letter) {
			case 'a':
			case 'A':
				return '\0'; // TODO
			case 'b':
			case 'B':
				return '8';
			case 'e':
			case 'E':
				return '3';
			case 'i':
			case 'I':
				return '!';
			case 'l':
			case 'L':
				return '1';
			case 'o':
			case 'O':
				return '0';
			case 's':
			case 'S':
				return random ? '5' : '$';
			case 't':
			case 'T':
				return '7';
			default:
				return '\0'; // TODO
		}
	}

	public static String toLeetSpeak(String text, double threshold) {
		return null; // TODO
	}

	public static String toLeetSpeak(String text) {
		return null; // TODO
	}

	public static void toLeetSpeakMemoryIntensive(Path input, Path output) throws IOException {
		// TODO
	}

	public static void toLeetSpeakMemoryIntensiveStream(Path input, Path output) throws IOException {
		// TODO
	}

	public static void toLeetSpeak(Path input, Path output) throws IOException {
		// TODO
	}

	public static void main(String[] args) throws IOException {
		String text = "Sally sells seashells at the sea shore.";
		System.out.println(text);
		System.out.println(toLeetSpeak(text));
		System.out.println(toLeetSpeak(text, 0.25));
		System.out.println(toLeetSpeak(text, 1.00));

//		String filename = EliteFileConverter.class.getSimpleName();
//		Path input = Path.of(".", "src", filename + ".java");
//		Path output = Path.of(".", "output", filename + ".txt");
//
//		Files.createDirectories(output.getParent());
//		toLeetSpeak(input, output);
//
//		Path nowhere = Paths.get("nowhere");
//		toLeetSpeak(nowhere, nowhere);
	}
}

package assign1;


/*
 * Data Compression - Assignment 1 - Huffman Compression
 * Written By:
 * Dor Avitan - 
 * Omer Sirpad -
 * Omer Amsalem - 
 */
public class Huffman {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HufmannEncoderDecoder h = new HufmannEncoderDecoder();
		long startTime=System.currentTimeMillis();
		String [] arr = {
				"D:\\programming\\Ass1ExampeInputs\\london_in_polish_source.txt",
				"D:\\programming\\Ass1ExampeInputs\\OnTheOrigin.txt",
				"D:\\programming\\Ass1ExampeInputs\\OnTheOrigin_C2.txt",
				"D:\\programming\\Ass1ExampeInputs\\YouKnowThisSound"};
		String [] arr1 = {
				"D:\\programming\\Ass1ExampeOutputs\\testcoded.txt",
				"D:\\programming\\Ass1ExampeInputs\\2.txt",
				"D:\\programming\\Ass1ExampeInputs\\3.txt",
				"D:\\programming\\Ass1ExampeInputs\\4.txt"};
		h.Compress(arr, arr1);
		String [] decInput = {
				"D:\\programming\\Ass1ExampeOutputs\\testcoded.txt",
				"D:\\programming\\Ass1ExampeOutputs\\OnTheOrigin.txt",
				"D:\\programming\\Ass1ExampeOutputs\\OnTheOrigin_C2.txt",
				"D:\\programming\\Ass1ExampeOutputs\\YouKnowThisSound"};
		String [] decOutput  = {
				"D:\\programming\\Ass1ExampeOutputs\\testdecoded.txt",
				"D:\\programming\\Ass1ExampeOutputs\\2.txt",
				"D:\\programming\\Ass1ExampeOutputs\\3.txt",
				"D:\\programming\\Ass1ExampeOutputs\\4.txt"};
		h.Decompress(decInput, decOutput);
		long Time=(System.currentTimeMillis()-startTime);
		System.out.println("Compressing+Decompressing took " + Time + " milliseconds");

	}

}

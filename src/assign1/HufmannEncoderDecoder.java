package assign1;
/*
 * Data Compression - Assignment 1 - Huffman Compression
 * Written By:
 * Dor Avitan - 
 * Omer Sirpad - 
 * Omer Amsalem - 
 */

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Assignment 1
 * Submitted by: 
 * Student 1. 	ID# XXXXXXXXX
 * Student 1. 	ID# XXXXXXXXX
 */

// Uncomment if you wish to use FileOutputStream and FileInputStream for file access.

import base.compressor;

public class HufmannEncoderDecoder implements compressor
{

	public HufmannEncoderDecoder()
	{

	}
	@Override
	public void Compress(String[] input_names, String[] output_names)//o(nlogn)	
	{
		HuffmanNode root=null;
		long startTime = System.currentTimeMillis();
		System.out.println("starting..");
		Path path = Paths.get(input_names[0]);//Get path from 1st array slot
		try
		{
			byte[] fileBytesArr = Files.readAllBytes(path);//Use imported class to read bytes to an array
			
			int freq[] = freqarr(fileBytesArr);//Create new frequency array for all possible bytes (255)
			root = HuffmanNode.BuildTree(freq);//Build Huffman tree

			//Build the dictionary
			HashMap <Byte,String > dictionary = new HashMap<Byte,String>();
			root.buildDictionary(dictionary,"");
			
			//write the coded version to string
			String code =codedString(fileBytesArr, dictionary);
			
			//actually write to the file
			
			writeStringToFile(code, output_names,root);
		}
		catch (IOException e) {  e.printStackTrace();}

		long endTime = System.currentTimeMillis();

		System.out.println("Compression took " + ((endTime - startTime)) + " miliseconds.\n");
	}
	
	public String codedString(byte [] fileBytesArr,HashMap<Byte, String> dictionary)
	{
		
		StringBuilder code=new StringBuilder();
		String s="";
		byte b;
		for (int j = 0; j < fileBytesArr.length; j++)
		{
			b=fileBytesArr[j];
			s = dictionary.get(b);//get the value of the letter and put into code
			code.append(s);
		}
		String retCode=code.toString();
		return retCode;

	}

	public int[] freqarr(byte[] fileBytesArr)
	{
		int freq[] = new int [256];
		
		//reads the file as bytes and count how many times the byte has appeared in the code
		for (int i = 0; i < fileBytesArr.length; i++)
		{
			int place=(int) fileBytesArr[i];
			if(place < 0) {
				place+=256;}
				freq[place] ++;
		}
		return freq;
	}


	public void writeStringToFile(String code, String[] output_names,HuffmanNode root)
	{

		try
		{
			DataOutputStream fos = new DataOutputStream(new FileOutputStream(output_names[0]));
			String tstr="";
			
			String treeString=root.writeTree(root,"");//characters string of tree (1 - leaf, 0 - internal node)
			int treeStringLength=treeString.length();
			
			fos.writeInt(treeStringLength);//Writes 4 bytes
			for(int i=0;i<treeStringLength;i++) fos.writeByte(treeString.charAt(i));//Writes the tree as bytes to the coded file
			
			
			int zeroCounter = 0;
			
			while (code.length() % 8 != 0)//Pad the code string with 0s so we can read it as a byte 
			{
				code += '0';
				zeroCounter++;
			}
			
			fos.write(zeroCounter);
			
			
			for(int i=0;i<code.length();i=i+8)
			{
				tstr=code.substring(i, i+8);//Take blocks of 8 character of 1s and 0s
				int bin=Integer.parseInt(tstr,2);//Parse the 8 chars to an int in binary format (radix 2)

				byte barr= (byte)bin;
				
				fos.writeByte(barr);//write the byte to the file
			}
			
			fos.close();
		}
		catch(Exception e){e.printStackTrace();}
	}

	@Override
	public void Decompress(String[] input_names, String[] output_names)
	{

		long startTimed = System.currentTimeMillis();
		System.out.println("starting Decompression");
		Path path = Paths.get(input_names[0]);//Get path from 1st array slot
		try
		{
			byte[] fileBytesArr = Files.readAllBytes(path);//Use imported class to read bytes to an array
			byte[] treeSizearr = {fileBytesArr[0],fileBytesArr[1],fileBytesArr[2],fileBytesArr[3]};//Size was written as Int (4 bytes), so need to combine back to int
			ByteBuffer inr = ByteBuffer.wrap(treeSizearr);
			int treeSize=inr.getInt();
			
			
			Queue<Byte> treeQ = new LinkedList<Byte>();
			for(int i=4;i<treeSize+4;i++){//Read bytes from after the size int (this is the tree size), and add to a Queue
				treeQ.add(fileBytesArr[i]);
			}
			
			HuffmanNode root=HuffmanNode.ReBuildTree(treeQ);//recreating the tree
			
			int code0Padding=fileBytesArr[4+treeSize];//retrieve the number of 0s used to pad the code(right after the tree code).
			
			StringBuilder codeString=new StringBuilder();
			for(int i=(4+treeSize+1);i<fileBytesArr.length;i++){//start reading the code (starting from after the tree size(4B),treeCode,0 Padding(1B)).
				byte temp=fileBytesArr[i];
				String tostring=Integer.toBinaryString((temp & 0xFF) + 0x100).substring(1); 
				//This handles special chars (extended ASCII). it does an AND with 0xFF (converting it to a number 0-255) and the 0x100 and substring(1) makes sure there will be leading 0s.
				codeString.append(tostring);
			}
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(output_names[0]));
			String codeString2=codeString.substring(0,codeString.length()-code0Padding).toString();//String witout padding 0s
			
			Queue<Character> binQ= new LinkedList<Character>();
			for(int i=0;i<codeString2.length();i++){
				binQ.add(codeString2.charAt(i)); // Adding code to a Queue for decoding
			}
			out.write((decode(binQ,root))); // decode and write as bytes
		
			out.close();
			
		}catch (IOException e) {
			e.printStackTrace();
			}

		long endTimed = System.currentTimeMillis();

		System.out.println("Decomp took " + ((endTimed - startTimed)) + " miliseconds \n");

	}


	
	public byte[] decode(Queue<Character> binQ,HuffmanNode root){
		/*Aux function in preparation for recursion. 
		* Gets Queue and root node and decodes the coded file
		*/
		StringBuilder decodedStr=new StringBuilder();//TODO: Consider using byte array
		while(!binQ.isEmpty()){
			decodedStr.append((char)decodeRec(root,binQ));
		}
		
		
			byte[] barr=new byte[decodedStr.length()];//Convert to byte array
			for(int i=0;i<decodedStr.length();i++)
			{
				barr[i]=(byte)decodedStr.charAt(i);
			}
			return barr;
	}
	public byte decodeRec(HuffmanNode root,Queue<Character> binQ){
		//The recursive decoding function which traverses the tree
		if(root.getLeft()==null && root.getRight()==null){
			return root.get_char();
		}
		if(binQ.poll()=='0'){
			return decodeRec(root.getLeft(),binQ);
		}

		return decodeRec(root.getRight(),binQ);
	}
	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names) {
		HuffmanNode root=null;
		System.out.println("starting..");
		Path path = Paths.get(input_names[0]);//Get path from 1st array slot
		try
		{
			byte[] fileBytesArr = Files.readAllBytes(path);//Use imported class to read bytes to an array
			
			int freq[] = freqarr(fileBytesArr);//Create new frequency array for all possible bytes (255)
			root = HuffmanNode.BuildTree(freq);//Build Huffman tree

			//Build the dictionary
			HashMap <Byte,String > dictionary = new HashMap<Byte,String>();
			root.buildDictionary(dictionary,"");
			
			//write the coded version to string
			String code =codedString(fileBytesArr, dictionary);
			
			//actually write to the file
			
			writeStringToFile(code, output_names,root);
			return code.getBytes();
		}
		catch (IOException e) {  e.printStackTrace();
		return null;}
		
	}
	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names) {

		System.out.println("starting Decompression");
		Path path = Paths.get(input_names[0]);//Get path from 1st array slot
		try
		{
			byte[] fileBytesArr = Files.readAllBytes(path);//Use imported class to read bytes to an array
			byte[] treeSizearr = {fileBytesArr[0],fileBytesArr[1],fileBytesArr[2],fileBytesArr[3]};//Size was written as Int (4 bytes), so need to combine back to int
			ByteBuffer inr = ByteBuffer.wrap(treeSizearr);
			int treeSize=inr.getInt();
			
			
			Queue<Byte> treeQ = new LinkedList<Byte>();
			for(int i=4;i<treeSize+4;i++){//Read bytes from after the size int (this is the tree size), and add to a Queue
				treeQ.add(fileBytesArr[i]);
			}
			
			HuffmanNode root=HuffmanNode.ReBuildTree(treeQ);//recreating the tree
			
			int code0Padding=fileBytesArr[4+treeSize];//retrieve the number of 0s used to pad the code(right after the tree code).
			
			StringBuilder codeString=new StringBuilder();
			for(int i=(4+treeSize+1);i<fileBytesArr.length;i++){//start reading the code (starting from after the tree size(4B),treeCode,0 Padding(1B)).
				byte temp=fileBytesArr[i];
				String tostring=Integer.toBinaryString((temp & 0xFF) + 0x100).substring(1); 
				//This handles special chars (extended ASCII). it does an AND with 0xFF (converting it to a number 0-255) and the 0x100 and substring(1) makes sure there will be leading 0s.
				codeString.append(tostring);
			}
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(output_names[0]));
			String codeString2=codeString.substring(0,codeString.length()-code0Padding).toString();//String witout padding 0s
			
			Queue<Character> binQ= new LinkedList<Character>();
			for(int i=0;i<codeString2.length();i++){
				binQ.add(codeString2.charAt(i)); // Adding code to a Queue for decoding
			}
			byte[] barr = decode(binQ,root);
			out.write(barr); // decode and write as bytes
		
			out.close();
			return barr;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
			}
	}

}

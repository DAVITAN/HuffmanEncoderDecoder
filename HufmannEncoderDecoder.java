package assign1;


import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
		System.out.println("starting..");//DEBUG
		Path path = Paths.get(input_names[0]);//Get path from 1st array slot
		try
		{
			byte[] arr = Files.readAllBytes(path);//Use imported class to read bytes to an array
			int freq[] = freqarr(arr);//Create new frequency array fro all possible bytes (255)
			root = HuffmanNode.BuildTree(freq);//o(nlogn)

			//Build the tree (recursive)

			long startTimeD=System.currentTimeMillis();
			HashMap <Character,String > dictionary = new HashMap<Character,String>();
			root.buildDictionary(dictionary,"");//o(nlogn)
			String s="";
			long endTimeD=System.currentTimeMillis();
			System.out.println("Building the Dictionary took " + ((endTimeD - startTimeD)/1000) + " seconds");

			//write the coded version to string
			long startTimeC=System.currentTimeMillis();
			String code =codedString(arr, dictionary);
			long endTimeC=System.currentTimeMillis();
			System.out.println("Building the code string took " + ((endTimeC - startTimeC)/1000) + " seconds");

			//actually write to the file
			System.out.println("Code Length: " + code.length()); //DEBUG
			testfile(code,"C:\\Users\\sirpa\\Documents\\eclipse-projects\\Huffman\\src\\assign1\\test1.txt");
			writeStringToFile(code, output_names,root);
		}
		catch (IOException e) {  e.printStackTrace();}

		long endTime = System.currentTimeMillis();

		System.out.println("Comp took " + ((endTime - startTime)/1000) + " seconds");
	}
	public void testfile(String code,String Filename)
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter( new FileWriter( Filename));
			writer.write(code);

		}
		catch ( IOException e)
		{
		}
		finally
		{
			try
			{
				if ( writer != null)
					writer.close( );
			}
			catch ( IOException e)
			{
			}
		}
	}
	/*
	 * @param byte array
	 * @param HashMap
	 * @return String
	 * returns a string of the coded version of the file
	 */
	public String codedString(byte [] arr,HashMap <Character,String > dictionary)
	{
		//String code = "";
		StringBuilder code=new StringBuilder();
		String s="";
		char c;
		for (int j = 0; j < arr.length; j++)//o(n)
		{
			c = (char)arr[j];
			s = dictionary.get(c);//o(1) get the value of the letter and put into code
			if (s == null)
				s = dictionary.get((char)(c + 256));
			//if (code == null)//will happen once
			//code = s;

			code.append(s);
		}
		String retCode=code.toString();
		return retCode;

	}
	/*
	 * @param: byte array
	 * @return: int array
	 * creates frequency array for all bytes
	 */
	public int[] freqarr(byte[] arr)
	{
		int freq[] = new int [256];
		long startTimeF=System.currentTimeMillis();
		//reads the file as bytes and count how many times the bite has appeared during the code
		for (int i = 0; i < arr.length; i++)//o(n)
		{
			if (arr[i] < 0)
				freq[arr[i]+256]++;
			else
				freq[(arr[i])] ++;
		}
		long endTimeF=System.currentTimeMillis();
		System.out.println("Analyzing freq took: " + (endTimeF-startTimeF)/1000 + " seconds.");
		return freq;
	}


	public void writeStringToFile(String code, String[] output_names,HuffmanNode root)
	{
		int zeroCounter = 0;
		System.out.println("code length: " + code.length());
		while (code.length() % 8 != 0)
		{
			code += '0';
			zeroCounter++;
		}
		try
		{
			FileOutputStream fos = new FileOutputStream(output_names[0]);
			String tstr="";
			fos.write((byte)zeroCounter);
			System.out.println("Code 0s: (before comp)" + zeroCounter);
			String treeString=root.writeTree(root,"");//Binary string of tree (1 - leaf, 0 - internal node)
			System.out.println("tree String (before): " + treeString);
			int treeStringSize0s=0;
			int treeString0s=0;
			String treeStringSize=Integer.toBinaryString(treeString.length());
			System.out.println("treeStringsize(before): "+treeStringSize);
			while(treeStringSize.length()%8!=0) {
				treeStringSize+='0';
				treeStringSize0s++;
			}
			while(treeString.length()%8!=0) {
				treeString+='0';
				treeString0s++;
			}

			fos.write((byte)(treeStringSize0s));//Number of 0s in the treeStringSize String
			//System.out.println("tree String Size (before comp): " + (int)Integer.parseInt(treeStringSize,2));
			fos.write((byte)Integer.parseInt(treeStringSize.substring(0, 8),2));
			fos.write((byte)Integer.parseInt(treeStringSize.substring(8, 16),2));
			System.out.println("tree string 0s : (before comp) : " + treeString0s);
			fos.write((byte)(treeString0s));//
			code=treeString+code;
			

			for(int i=0;i<code.length();i=i+8)//TODO
			{
				tstr=code.substring(i, i+8);
				int bin=Integer.parseInt(tstr,2);

				byte[] barr= {(byte)bin};
				fos.write(barr);
			}
			//Code: <zeroCounter(1byte)><treeStringSize0s(byte)><treeStringsize(2 bytes)><treeString0s(byte)><treeString (up to 511 bits)<code>

		}
		catch(Exception e){e.printStackTrace();}
	}

	@Override
	public void Decompress(String[] input_names, String[] output_names)
	{

		long startTimed = System.currentTimeMillis();
		System.out.println("starting Decompression");//DEBUG
		StringBuilder binStr=new StringBuilder();//BinaryString
		Queue<Character> binQ = new LinkedList<Character>();
		try {
			FileInputStream in = new FileInputStream(input_names[0]);
			//TODO: add a way to read the tree

			System.out.println("hereEEEERERERRER");
			int data = in.read();//Read characters from the coded file

			int leftover0s=0;
			leftover0s=data;//Find how many 0s we added		
			System.out.println("code zeros after dec: " + leftover0s);
			////////////////////////////////////////////////
			data=in.read();
			int TreeStringSize0s=data;


			data=in.read();
			String tmp=Integer.toBinaryString(data);
			while (tmp.length() <8) tmp = '0'+tmp;
			//if(tmp.length()<8) tmp=String.format("%8s", tmp).replace(' ', '0');
			String treeStringSize=tmp;
			data=in.read();
			tmp=Integer.toBinaryString(data);
			if(tmp.length()<8) tmp=String.format("%8s", tmp).replace(' ', '0');
			treeStringSize+=tmp;

			treeStringSize=treeStringSize.substring(0,(treeStringSize.length()-TreeStringSize0s));

			int treeStringSizeInt=Integer.parseInt(treeStringSize, 2);
			System.out.println("tree string size after dec: " + treeStringSizeInt);

			data=in.read();
			int treeString0s=data;
			String treeString="";
			int dor = treeStringSizeInt-treeString0s;
			System.out.println("dor: "+dor);
			for(int i=0;i<((treeStringSizeInt)*8-treeString0s);i+=8){
				data=in.read();
				String tmp2=Integer.toBinaryString(data);
				if(tmp2.length()<8) tmp2=String.format("%8s", tmp2).replace(' ', '0');
				treeString+=tmp2;
			}
			data=in.read();
			//////////////////////////////////////////////////
			while(data != -1) {
				String temp=Integer.toBinaryString(data);
				if(temp.length()<8) temp=String.format("%8s", temp).replace(' ', '0');

				binStr.append(temp);  
				data = in.read();
			}
			in.close();
			testfile(binStr.toString(),"C:\\Users\\sirpa\\Documents\\eclipse-projects\\Huffman\\src\\assign1\\test2.txt");

			//Breaking the metadata<5 bytes>
			//Code: <zeroCounter(1byte)><treeStringSize0s(3bits)><treeStringsize(2 bytes)><treeString0s(byte)><treeString (up to 511 bits)<code>
			String temp="";
			Queue<Character> treeQ = new LinkedList<Character>();
			int counter=0;
			String s="";
			for(int i=0;i<treeString.length();i++)
			{
				counter++;
				treeQ.add(treeString.charAt(i));
				s+=treeString.charAt(i);
			}
			testfile(s, "C:\\Users\\sirpa\\Documents\\eclipse-projects\\Huffman\\src\\assign1\\tree.txt");



			//Build tree from string (inorder? traversal (node-left-right);

			HuffmanNode root=null;
			root = HuffmanNode.ReBuildTree(treeQ);
			String ss = "";
			while(!(treeQ.isEmpty()))
			{
				ss+=treeQ.poll();
			}
			testfile(ss, "C:\\Users\\sirpa\\Documents\\eclipse-projects\\Huffman\\src\\assign1\\ss.txt");//(DELETE)
			//		System.out.println("dec code length : " + (binStr.length()-(30+treeStringSize)));
			System.out.println(' ');
			for(int i=32+treeString.length(); i<binStr.length()-leftover0s; i++){
				binQ.add(binStr.charAt(i));
			}


			Writer writer;

			writer = new FileWriter(output_names[0]);
			writer.write(decode(binQ,root));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		long endTimed = System.currentTimeMillis();

		System.out.println("Decomp took " + ((endTimed - startTimed)/1000) + " seconds");

	}


	public String decode(Queue<Character> binQ,HuffmanNode root){
		String decodedStr="";
		System.out.println("Decoding...");
		while(!binQ.isEmpty()){
			decodedStr=decodedStr + decodeRec(root,binQ);
		}
		return decodedStr;
	}
	public char decodeRec(HuffmanNode root,Queue<Character> binQ){
		if(root.getLeft()==null && root.getRight()==null){
			return root.get_char();
		}
		if(binQ.poll()=='0'){//PROBLEM: Gets to null
			return decodeRec(root.getLeft(),binQ);
		}

		return decodeRec(root.getRight(),binQ);
	}
	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names)
	{
		long startTime = System.currentTimeMillis();
		Path path = Paths.get(input_names[0]);
		byte[] arr = null;
		try
		{
			arr = Files.readAllBytes(path);
		} 
		catch (IOException e) {e.printStackTrace();}
		int freq[] = freqarr(arr);
		HuffmanNode root = HuffmanNode.BuildTree(freq);//o(nlogn)

		HashMap <Character,String > dictionary = new HashMap<Character,String>();

		root.buildDictionary(dictionary,"");//o(nlogn)

		//write the coded version to string
		String code =codedString(arr, dictionary);

		//actually write to the file
		writeStringToFile(code, output_names,root);
		long endTime = System.currentTimeMillis();

		System.out.println("That took " + ((endTime - startTime)/1000) + " seconds");
		return (code.getBytes());
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names)
	{
		return null;
	}

}

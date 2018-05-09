package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CharToBin {

	public static void main(String[] args) {
		//Convert chars to binary 
		Path path = Paths.get("D:\\programming\\Ass1ExampeInputs\\london_in_polish_source.txt");//Get path from 1st array slot
		try
		{
			byte[] fileBytesArr = Files.readAllBytes(path);
			System.out.println(fileBytesArr.toString());
		}catch (IOException e){
			e.printStackTrace();
		}
		/*
		char[] c={203,156,210,152};
		String str="";
		for(int i=0;i<c.length;i++){
		System.out.println((int)c[i]);
		str = str +Integer.toBinaryString(c[i]);}
		System.out.println(str);
		*/
		String str="";
		String temp="";
		System.out.println((char)0x255C);
		while(temp.length()!=511){
			temp+='0';
		}
		try {
			DataOutputStream fos = new DataOutputStream(new FileOutputStream("D:\\programming\\GIT\\HuffVer3_1\\1\\OutputTests\\fos.txt"));
			fos.writeByte('¿');
			/*
			StringBuilder toFile=new StringBuilder("10101001010");
			while (toFile.length()%8!=0) toFile.insert(((int)toFile.length()/8)*8, '0');
			for(int i=0;i<toFile.length();i=i+8) {
				String toFilestr=toFile.substring(i, i+8);
				toFilestr
				byte btof=(byte)Integer.parseInt(toFilestr,2);
				fos.write(btof);
			
			}*/
			/*String sizebin = Integer.toBinaryString(size);
			
			fos.write((byte)Integer.parseInt(sizebin.substring(0,8),2));
			fos.write((byte)Integer.parseInt(sizebin.substring(8,sizebin.length()),2));*/
			//fos.close();
			DataInputStream fis = new DataInputStream(new FileInputStream("D:\\programming\\GIT\\HuffVer3_1\\1\\OutputTests\\fos.txt"));
			int ifromf=0;
			byte bfromf;
			bfromf=fis.readByte();
			fos.write(bfromf);
			ifromf=fis.read();
			if(ifromf<0)
				ifromf=(ifromf+256);
			String fromfile=Integer.toBinaryString(ifromf);
			ifromf=fis.read();
			fromfile+=Integer.toBinaryString(ifromf);
			System.out.println("from file : " + fromfile);
			
			System.out.println("baaahh " + fis.read());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(temp.length() + " length(int)");
		int size=temp.length();
		System.out.println((byte)8 + " length(char)");
		int sized=size;
		String treeStringSize=Integer.toBinaryString(temp.length());
		while(treeStringSize.length()%8!=0) {treeStringSize+='0';}
		if(treeStringSize.length()%8!=0) System.out.println("nope");
		System.out.println("length fixed: "+treeStringSize);
		String walla=Integer.toBinaryString(size);
		System.out.println(walla + " length(binary)");

		try {
			InputStream in = new FileInputStream("c:\\data\\1.txt");
			int data = in.read();
			while(data != -1) {
			  //do something with data...
				str=str+Integer.toBinaryString(data);
			  
			  data = in.read();}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(str);
		String tstr="";
		
			tstr=str.substring(0, 7);
			int c=Integer.parseInt(tstr,2);
			
			System.out.println(c);
			System.out.print((char)c);
		
		
		
		
	}

}

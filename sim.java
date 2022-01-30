// Chase Harwell
// 4614181
// EEL 4768 Fall 2021

import java.util.*;
import java.io.*;
import java.lang.*;

public class sim
{

	// References for converting binary addresses to decimal
	private static final String referenceHex = "0123456789ABCDEF";

	public sim(String GPB, String RB, String file)
	{

		int M = Integer.parseInt(GPB);
		int N = Integer.parseInt(RB);
		int ghb = 0;
		boolean taken;
		int misses = 0;
		int total = 0;

		// There will be 2^M entries in the table
		int table_size = (int)Math.pow(2,M);
		int[] table = new int[table_size];

		// Set every table vale to 2 (weakly true)
		for (int i = 0; i < table_size; i++)
		{
			table[i] = 2;
		}

		// Find the input file and throw an exception if it doesn't exist
		File input;
		Scanner in;
		try {
			input  = new File(file);
			in = new Scanner(input);
		} catch (FileNotFoundException e) {
			System.out.println("Error: File does not exist!");
			return;
		}


		while(in.hasNext())
		{
			String address = in.next();
			String taken_val = in.next();

			if (taken_val.equals("t"))
				taken = true;
			else
				taken = false;

			// Shift left 2 bits for offset
			int address_decimal = hextoDecimal(address);
			address_decimal = address_decimal/4;

			// Get the index in the table for this specific entry
			int mindex = address_decimal % ((int)Math.pow(2,M));
			int n_ext = ghb << (M-N);
			int index = mindex ^ n_ext;

			// Set the prediction to the value located in the index of the table
			int prediction = table[index];


			// This occurs when the next line in the trace file predicts taken
			if (taken)
			{
				// Shift everything right but make the leading value a 1 since taken is true
				ghb = (ghb>>1) + (int)Math.pow(2,N-1);

				// Determine if the prediction was correct
				if (prediction >= 2)
				{
					total++;
				}
				else
				{
					total++;
					misses++;
				}

				// Increment the prediction value in the table
				prediction++;
				if (prediction > 3)
				{
					prediction = 3;
				}

			}

			// This occurs when the next line in the trace file predicts not taken
			else
			{
				// Shift everything right. This will make the leading value a 0 since taken is false
				ghb = ghb>>1;

				// Determine if the prediction was correct
				if (prediction <= 1)
				{
					total++;
				}
				else
				{
					total++;
					misses++;
				}

				// Decrement the value in the table
				prediction--;
				if (prediction < 0)
				{
					prediction = 0;
				}

			}

			// Set the value of the table to the newly assigned prediction
			table[index] = prediction;

		}

		// Calculates miss rate and prints the stats to the console
		float fl_misses = (float)misses;
		float fl_total = (float)total;

		float misspredict = fl_misses/fl_total;
		System.out.printf("%d %d %.4f", M, N, misspredict);
		System.out.println();

	}

	// Takes in a hexadecimal address in String form and converts it to an integer
	public static int hextoDecimal(String address)
	{
		address = address.toUpperCase();

		int current_val = 0;
		for (int i = 0; i < address.length(); i++)
		{
			char current = address.charAt(i);
			int value = referenceHex.indexOf(current);
			current_val = current_val*16 + value;
		}

		return current_val;
	}

	public static void main(String[] args)
	{
		sim sim = new sim(args[0], args[1], args[2]);
	}
}

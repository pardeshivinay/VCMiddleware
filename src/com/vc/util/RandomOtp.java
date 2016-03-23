package com.vc.util;

import java.util.Random;

public class RandomOtp {

	
		private static final int[] pins = new int[900000];
		 
		static {
		    for (int i = 0; i < pins.length; i++)
		        pins[i] = 100000 + i;
		}
		 
		private static int pinCount;
		 
		private static final Random random = new Random();
		 
		public int generatePin(){
		    if (pinCount >= pins.length)
		        throw new IllegalStateException();
		    int index = random.nextInt(pins.length - pinCount) + pinCount;
		    int pin = pins[index];
		    pins[index] = pins[pinCount++];
		    return pin;
		}
		
		
		public static String generateCardNumber(){
		    Random rnd = new Random();
		    int counter=0;
		    StringBuffer stringBuffer = new StringBuffer();
		    while(counter<=16){
		        int generate = rnd.nextInt(9); 
		        stringBuffer.append(generate);
		        counter++;
		    }
		    return stringBuffer.toString();
		}

	

}


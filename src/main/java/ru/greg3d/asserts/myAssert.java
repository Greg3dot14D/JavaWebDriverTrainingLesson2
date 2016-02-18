package ru.greg3d.asserts;

import org.testng.SkipException;

public abstract class myAssert {
	public static void Ignore(String arg0){
		throw new SkipException(arg0);
	}
}

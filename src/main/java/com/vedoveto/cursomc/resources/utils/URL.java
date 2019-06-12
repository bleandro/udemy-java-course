package com.vedoveto.cursomc.resources.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class URL {
	public static List<Integer> decodeIntList(String str, String delimiter)
	{
		return Arrays.asList(str.split(delimiter)).stream().map((value) -> Integer.parseInt(value))
				.collect(Collectors.toList());
	}

	public static String decodeParam(String param)
	{
		try 
		{
			return URLDecoder.decode(param, "UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{
			return "";
		}
	}
}

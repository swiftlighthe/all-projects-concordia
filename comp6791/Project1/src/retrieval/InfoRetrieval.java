/*
 * COMP6791 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 * 
 */

package retrieval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import parser.InvertedIndex;
import parser.Stemmer;
import parser.URLList;

import utils.ByteArrayWrapper;
import utils.SysLogger;

public class InfoRetrieval {
	// array of the last term of the merged inverted index file and the file name
	// "null,c:\\inverted-index-0"		// first one 
	// "term,c:\\inverted-index-1"
	public static ArrayList<String> arrTerm2File = new ArrayList<String>();
	
//	// array of the last docID of the raw file and the file name
//	// "3008,c:\\reut2-000.sgm"
//	public static ArrayList<String> arrDocID2File = new ArrayList<String>();
	
	// inverted index in memory for the first file
	// because the memory size only allowed to load one file
	// we keep the data of first file in memory
	private HashMap<ByteArrayWrapper, InvertedIndex> mapDic = new HashMap<ByteArrayWrapper, InvertedIndex>();
	
	public static final int MAX_NUMBER_FILES_RETRIEVAL = 20;
	public static String filenameResult = "";
	
	//public OkapiBM25 bm25 = new OkapiBM25();

	public int init() {
		// load the inverted index into memory from the first file
		String[] tmp = arrTerm2File.get(0).split(","); 
		long numTerms = 0, numPostings = 0;
		
		try {									
			BufferedReader in = new BufferedReader(new FileReader(tmp[1]));
			while(true) {
				String buf = in.readLine();
				if (buf == null) {
					in.close();
					//System.out.println("--" + numTerms + ", " + numPostings);
					
					OkapiBM25.calcAvgDocLen();
					return -1;
				}
				tmp = buf.split(",");
				ByteArrayWrapper term = new ByteArrayWrapper(tmp[0].getBytes());
				InvertedIndex idx = new InvertedIndex();
				int cnt = Integer.parseInt(tmp[1]);				
				long[] postings = new long[cnt];
				
				idx.docFreq = cnt;
				for (int j = 2; j < tmp.length; j++) {
					postings[j - 2] = Long.parseLong(tmp[j]);
				}
				idx.postings = postings;

				mapDic.put(term, idx);
				numTerms++;
				numPostings += cnt;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return -1;
		}
		
	}
	
	public InvertedIndex getPostingsFromFiles(String term) {
		// find the filename that has the term
		String filename = null;
		for (int i = 1; i < arrTerm2File.size(); i++) {		// start from the second file
			String[] tmp = arrTerm2File.get(i).split(","); 
			if (term.compareTo(tmp[0]) <= 0) {
				filename = tmp[1];
				break;
			}
		}
		
		// do not find any file that contains the term
		if (filename == null) {
			return null;
		}
		
		// load the inverted index file
		try {									
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while (true) {
				String buf = in.readLine();
				if (buf == null) {
					return null;
				}
				String[] tmp = buf.split(",");
				
				if (!tmp[0].equals(term)) {
					continue;
				}
				
				// find the term, return the postings
				InvertedIndex idx = new InvertedIndex();
				int cnt = Integer.parseInt(tmp[1]);				
				long[] postings = new long[cnt];
				
				idx.docFreq = cnt;
				for (int j = 2; j < tmp.length; j++) {
					postings[j - 2] = Long.parseLong(tmp[j]);
				}
				idx.postings = postings;
				return idx;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return null;
		}
	}
	
	// get the intersection of postings list
	private ArrayList<Long> getIntersectionOfDocID(InvertedIndex[] arrIdx) {
		// first find the smallest docFreq one
		int k = 0;
		for (int i = 1; i < arrIdx.length; i++) {
			if (arrIdx[k].docFreq > arrIdx[i].docFreq) {
				k = i;
			}
		}
		
		ArrayList<Long> ret = new ArrayList<Long>();
		long[] s = arrIdx[k].postings;
		for (int i = 0; i < s.length; i++) {
			int findIt = 0;
			for (int j = 0; j < arrIdx.length; j++) {
				if (k == j) {
					continue;
				}
				for (int j2 = 0; j2 < arrIdx[j].postings.length; j2++) {
					if (s[i] == arrIdx[j].postings[j2]) {
						findIt++;
						break;
					}
				}
			}
			if (findIt == arrIdx.length - 1) {
				ret.add(s[i]);
			}
		}
		
		return ret;
	}
	
	// get union of two postings lists for the terms
	// e.g. (Black Monday) OR (Wall Street crisis)
	private ArrayList<Long> getUnionOfDocID(ArrayList<Long> left, ArrayList<Long> right) {
		ArrayList<Long> ret = new ArrayList<Long>();
		
		// we need to sort the postings
		int i = 0, j = 0;
		while (true) {
			if (i < left.size() && j < right.size()) {
				if (left.get(i).longValue() <= right.get(j).longValue()) {
					ret.add(left.get(i++));					
				} else {
					ret.add(right.get(j++));
				}
			} else if (i == left.size() && j < right.size()) {
				ret.add(right.get(j++));
			} else if (j == right.size() && i < left.size()) {
				ret.add(left.get(i++));
			} else {
				break;
			}
		}
	
		return ret;
	}
	
	// get negation between two postings of the terms
	// e.g. (Black Monday) AND NOT (Wall Street crisis)
	private ArrayList<Long> getNegationOfDocID(ArrayList<Long> left, ArrayList<Long> right) {
		ArrayList<Long> ret = new ArrayList<Long>(left);
		
		for (int i = 0; i < left.size(); i++) {
			for (int j = 0; j < right.size(); j++) {
				if (left.get(i).longValue() == right.get(j).longValue()) {
					ret.remove(i);
				}
			}
		}
		
		return ret;
	}
	
	private BufferedReader fileIn = null;
	private String lastFilename = "";
	private String preDocID = "";
	private StringBuffer getDocContentFromFile(String filename, String docID) {
		StringBuffer sbRet = new StringBuffer();
		String docIDXMLTag = "NEWID=\"" + docID + "\">";

		// if the file has not been opened, open it
		// if the docID < preDocID, reopen it
		if (fileIn == null || !lastFilename.equals(filename)
				|| preDocID.compareToIgnoreCase(docID) > 0) {
			try {			
				if (fileIn != null) {
					fileIn.close();
				}
				fileIn = new BufferedReader(new FileReader(filename));
				lastFilename = filename;
			} catch (Exception e) {
				e.printStackTrace();
				SysLogger.err(e.getMessage());
				SysLogger.err(filename + ", " + docID);
				return sbRet;
			}
		}
		preDocID = docID;
		
		if (fileIn == null) {
			SysLogger.err("getDocDataFromFile: file == null");
			SysLogger.err(filename + ", " + docID);
			return sbRet;
		}
		
		// find out the doc ID from the file
		boolean bCopying = false;
		while (true) {
			try {
				String buf = fileIn.readLine();
				if (buf == null) {
					fileIn.close();
					break;
				}
				
				if (!bCopying) {
					if (buf.indexOf(docIDXMLTag) == -1) {
						continue;		// do not find the xml tag for docID
					}
					bCopying = true;
				}
				
				// copy the data
				sbRet.append(buf + "\n");
				if (buf.equals("</REUTERS>")) {
					break;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				SysLogger.err(e.getMessage());
				SysLogger.err(filename + ", " + docID);
				break;
			}
		}

		return sbRet;
	}
	
	// get doc content
	private StringBuffer getDocContent(ArrayList<Long> docIDs) {
		StringBuffer sbRet = new StringBuffer();
		
		int j = 0;
		
		// traverse list of docID 
		for (; j < docIDs.size(); j++) {
			long dID = docIDs.get(j);

/*
			// traverse all raw files
			for (int i = 0; i < arrDocID2File.size(); i++) {
				String[] tmp = arrDocID2File.get(i).split(",");
				long docID = Long.parseLong(tmp[0]);

				if (dID <= docID) {
					sbRet.append(getDocContentFromFile(tmp[1], dID + ""));
					sbRet.append("\n");
					break;
				}
			}*/
			
			sbRet.append(URLList.getURL(dID));
			sbRet.append("\n");
		}
		return sbRet;
	}
	
	// remove '(' ')'
	private String removeParens(String terms) {
		if (terms.charAt(0) == '(') {
			terms = terms.substring(1, terms.length() - 1);
		}
		return terms;
	}
	
	// parse the query to support OR and AND NOT
	// input params: query
	// output params: termDocFreq, which is a map. the key is the term, and 
	// 	              the value of the key is the doc frequency of the term.
	// return a list of doc IDs
	public ArrayList<Long> parseQuery(String query, 
			HashMap<String, Integer> termDocFreq) {
		String[] subQuery = query.trim().split("OR");
		
		if (subQuery.length == 1) {
			subQuery = query.trim().split("AND NOT");
			if (subQuery.length == 1) {
				String[] terms = removeParens(subQuery[0].trim().toLowerCase()).trim().split(" ");
				return getIntersectionPostingsOfTerms(terms, termDocFreq);
				
			} else {
				// a AND NOT b
				String[] leftTerms = removeParens(subQuery[0].trim().toLowerCase()).trim().split(" ");
				String[] rightTerms = removeParens(subQuery[1].trim().toLowerCase()).trim().split(" ");
				
				return getNegationOfDocID(getIntersectionPostingsOfTerms(leftTerms, termDocFreq), 
						getIntersectionPostingsOfTerms(rightTerms, termDocFreq));
			}
			
		} else {
			// a OR b
			String[] leftTerms = removeParens(subQuery[0].trim().toLowerCase()).trim().split(" ");
			String[] rightTerms = removeParens(subQuery[1].trim().toLowerCase()).trim().split(" ");
			
			return getUnionOfDocID(getIntersectionPostingsOfTerms(leftTerms, termDocFreq), 
					getIntersectionPostingsOfTerms(rightTerms, termDocFreq));
		}
	}
	
	// input params: terms, which is an array of terms
	// output params: termDocFreq, which is a map. the key is the term, and 
	// 	              the value of the key is the doc frequency of the term.
	// return a list of doc IDs
	private ArrayList<Long> getIntersectionPostingsOfTerms(String[] terms, 
			HashMap<String, Integer> termDocFreq) {
		InvertedIndex[] arrIdx = new InvertedIndex[terms.length];
		Stemmer s = new Stemmer();
		
		// get all postings
		for (int i = 0; i < terms.length; i++) {
			// stemmer each term
			String stemmedTerm;
			s.add(terms[i].toCharArray(), terms[i].length());
			s.stem();
			stemmedTerm = s.toString();

			// look up the term in memory first
			arrIdx[i] = mapDic.get(new ByteArrayWrapper(stemmedTerm.getBytes()));
			
			// try to find it in files
			if (arrIdx[i] == null) {
				arrIdx[i] = getPostingsFromFiles(stemmedTerm);
				if (arrIdx[i] == null) {
					//sbRet.append("Not found any result for the term: " + stemmedTerm);
					return null;
				}
			}
			
			// store data for bm25
			termDocFreq.put(stemmedTerm, arrIdx[i].docFreq);
			
			// sysloger
			SysLogger.info("Get postings for " + terms[i] + ":");
			StringBuffer msg = new StringBuffer();
			for (int j = 0; j < arrIdx[i].postings.length; j++) {
				msg.append(arrIdx[i].postings[j] + ", ");
			}
			SysLogger.info(msg.toString());
		}
		
		// compare the postings, find the intersection of the docID
		return getIntersectionOfDocID(arrIdx);
	}

	public int getDF(String term) {
		InvertedIndex idx = mapDic.get(new ByteArrayWrapper(term.getBytes()));
		return idx.docFreq;
	}

	public StringBuffer search(String query) {
		StringBuffer sbRet = new StringBuffer();		
		
		sbRet.append("Query: " + query + "\n\n");
		if (query == null || query.length() < 1) {
			sbRet.append("Wrong query, please check it.");
			return sbRet;
		}
		
		// doc frequency of terms in query
		HashMap<String, Integer> termDocFreq = new HashMap<String, Integer>();
		ArrayList<Long> docIDs = parseQuery(query, termDocFreq);

		if (docIDs == null || docIDs.size() < 1) {
			sbRet.append("Not found any postings for the terms");
			return sbRet;
		}

		// get scored doc list
		HashMap<Long, Double> mapScoredDocIDs = OkapiBM25.getScoredDocIDs(termDocFreq, docIDs);
		ArrayList<Long> scoredDocIDs = new ArrayList<Long>();
		for (Long key : mapScoredDocIDs.keySet()) {
			scoredDocIDs.add(key);
		}
		
		// show the list of doc id to user
		sbRet.append("Search result:\nDocument ID list (In total: " + scoredDocIDs.size() + "): \n");
		for (int i = 0; i < scoredDocIDs.size(); i++) {
			Long key = scoredDocIDs.get(i); 
			sbRet.append(String.format("%5s (%7.4f) ", 
					key.toString(), mapScoredDocIDs.get(key)));
			
			if (i % 6 == 5) {
				sbRet.append("\n");
			}
		}
		SysLogger.info(sbRet.toString());
		sbRet.append("\n\n");

		// get doc from raw files
		// close the filehandle first
		if (fileIn != null) {
			try {
				fileIn.close();
			} catch (IOException e) {
				e.printStackTrace();
				SysLogger.err(e.getMessage());
			}
			fileIn = null;
		}		
		sbRet.append(getDocContent(scoredDocIDs));
		
		//
		sbRet.append("\n\nAll the results have been stored to file:\n");
		sbRet.append(filenameResult + "\n");
		sbRet.append("\n");

		// output the result to file
		try {
			BufferedWriter fileResult = new BufferedWriter(new FileWriter(filenameResult)); 
			fileResult.write(sbRet.toString());
			fileResult.close();
		} catch (IOException e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
		}
		
		return sbRet;
	}

}

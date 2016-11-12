package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	
	
	private TagNode build2(){
		if(!sc.hasNext()){
			return null;
		}
		String tag=sc.nextLine();
		TagNode child=null;
		if(tag.contains("</")&&tag.charAt(tag.length()-1)=='>'){
			return null;
		}
		if(tag.charAt(0)=='<'&&tag.charAt(tag.length()-1)=='>'){
			tag=tag.substring(1,tag.length()-1);
			child=build2();
		}
		TagNode sibling=build2();
		TagNode par=new TagNode(tag,child,sibling);
		return par;
	}
	
	public void build() {
		root=build2();
	}
	
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		replaceTag(oldTag,newTag,root);
	}
	
	private void replaceTag(String oldTag, String newTag, TagNode tmp){
		for (TagNode ptr=tmp; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild != null) {
				if(oldTag.equals(ptr.tag))
					ptr.tag=newTag;
			}
			replaceTag(oldTag,newTag,ptr.firstChild);
		}
		
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	//WHAT IF THERE ARE TWO TABLES DO WE NEED TO BOLD THE ROW OF THE SECOND TABLE
	private void boldRow(int row, int count,TagNode tmp){
		for (TagNode ptr=tmp; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild != null) {
				if(ptr.tag.equals("tr"))
					count++;
				if(count==row){
					if(ptr.tag.equals("td")){
						TagNode tg=new TagNode("b",ptr.firstChild,null);
						ptr.firstChild=tg;
					}
				}
			}
			boldRow(row,count,ptr.firstChild);
		}	
	}
	
	public void boldRow(int row) {
		int count=0;
		boldRow(row,count,root);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	
	public void removeTag(String tag) {
		if(tag.equals("p")||tag.equals("em")||tag.equals("b"))
			case1(tag,root);
		else if(tag.equals("ol")||tag.equals("ul"))
			case2(tag,root);
	}
	
	private void case2(String tag, TagNode tmp){
		for (TagNode ptr=tmp; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild != null) {
				case2(tag,ptr.firstChild);
				if(tag.equals(ptr.tag)){
					TagNode tmp2=ptr.firstChild,sib=ptr.firstChild.sibling,sib2=tmp2,orig=ptr.sibling,prev=sib2;
					while(sib2!=null){
						if(sib2.tag.equals("li")){
							sib2.tag="p";
						}
						prev=sib2;
						sib2=sib2.sibling;
					}
					ptr.tag=ptr.firstChild.tag;
					ptr.firstChild=tmp2.firstChild;
					if(sib!=null){
						ptr.sibling=sib;
						prev.sibling=orig;
					}
				}
			}
		}
	}
	
	private void case1(String tag,TagNode tmp){
		for (TagNode ptr=tmp; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild != null) {
				case1(tag,ptr.firstChild);
				if(tag.equals(ptr.tag)){
					TagNode tmp2=ptr.firstChild,sib=ptr.firstChild.sibling,sib2=tmp2,orig=ptr.sibling;
					while(sib2.sibling!=null){
						sib2=sib2.sibling;
					}
					ptr.tag=ptr.firstChild.tag;
					ptr.firstChild=tmp2.firstChild;
					if(sib!=null){
						ptr.sibling=sib;
						sib2.sibling=orig;
					}
				}
			}
		}
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	
	private void addTag(String word, String tag, TagNode tmp){
		for (TagNode ptr=tmp; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				String checkCase=ptr.tag.toLowerCase(), checkWord=word.toLowerCase();
					if(checkCase.contains(checkWord)){
						//three cases beginning of string, middle of string, end of string
						char[] charArr=checkCase.toCharArray();
						int i=0;
						int d=0;
						for(int p=0;p<charArr.length;p++){
							if(charArr[p]==checkWord.charAt(i)){
								i++;
							}
							else{
								d+=i;
								i=0;
								d++;
								
							}
							if(p+1>=word.length()&&i==word.length()&&!checkCase.equals(checkWord)){
								
								if(p+1==word.length()){
									if(charArr[p+1]!=' '&&charArr[p+1]!='.'&&charArr[p+1]!=','&&charArr[p+1]!='?'&&charArr[p+1]!='!'&&charArr[p+1]!=':'&&charArr[p+1]!=';'){
										d+=i;
										i=0;
									}
								}
								else if(charArr[p-word.length()]!=' '){
									d+=i;
									i=0;
								}
							}
							
							if(i==word.length()){
								if((p+1==charArr.length)||(p+1<charArr.length&&charArr[p+1]==' ')){
									if(word.length()-1-p==0){
										String word1=ptr.tag.substring(0,p+1);
										if(p+1!=charArr.length){
											String word2=ptr.tag.substring(p+1);
											TagNode sib=new TagNode(word2,null,ptr.sibling);
											ptr.sibling=sib;
										}
										ptr.tag=tag;
										ptr.firstChild=new TagNode(word1,null,null);
										ptr=ptr.sibling;
										
									}
									else if(p+1==charArr.length){
										String word1=ptr.tag.substring(0,d),word2=ptr.tag.substring(d);
										TagNode tag2=new TagNode(word2,null,null);
										TagNode tg=new TagNode(tag,tag2,ptr.sibling);
										ptr.tag=word1;
										ptr.sibling=tg;
										ptr=ptr.sibling.sibling;
										
									}
									else{
										String word1=ptr.tag.substring(0,d),word2=ptr.tag.substring(d,d+word.length()),word3=ptr.tag.substring(d+word.length());
										TagNode tag2=new TagNode(word2,null,null);
										TagNode tag3=new TagNode(word3,null,ptr.sibling);
										TagNode tg=new TagNode(tag,tag2,tag3);
										ptr.tag=word1;
										ptr.sibling=tg;
										ptr=ptr.sibling.sibling;
										
									}
									d=0;
								}
								else if(((p+2==charArr.length)||(p+2<charArr.length&&charArr[p+2]==' '))&&(charArr[p+1]=='.'||charArr[p+1]==','||charArr[p+1]=='?'||charArr[p+1]=='!'||charArr[p+1]==':'||charArr[p+1]==';')){
									
									if(word.length()-1-p==0){
										String word1=ptr.tag.substring(0,p+2);
										if(p+2!=charArr.length){
											String word2=ptr.tag.substring(p+2);
											TagNode sib=new TagNode(word2,null,ptr.sibling);
											ptr.sibling=sib;
										}
										ptr.tag=tag;
										ptr.firstChild=new TagNode(word1,null,null);
										ptr=ptr.sibling;
										d=-1;
										
									}
									else if(p+2==charArr.length){
										String word1=ptr.tag.substring(0,d),word2=ptr.tag.substring(d);
										TagNode tmp2=ptr;
										TagNode tag2=new TagNode(word2,null,null);
										TagNode tg=new TagNode(tag,tag2,ptr.sibling);
										ptr.tag=word1;
										tmp2.sibling=tg;
										ptr=ptr.sibling.sibling;
										d=-1;
									}
									else{
										String word1=ptr.tag.substring(0,d),word2=ptr.tag.substring(d,d+word.length()+1),word3=ptr.tag.substring(d+word.length()+1);
										
										TagNode tag2=new TagNode(word2,null,null);
										TagNode tag3=new TagNode(word3,null,ptr.sibling);
										TagNode tg=new TagNode(tag,tag2,tag3);
										ptr.tag=word1;
										ptr.sibling=tg;
										ptr=ptr.sibling.sibling;
										d=-1;
									}
								}
								else
									d+=i;
								i=0;
							}
						}
					}
			}
			if(ptr==null){
				break;
			}
				addTag(word,tag,ptr.firstChild);	
		}	
	}
	
	public void addTag(String word, String tag) {
		addTag(word,tag,root);
		System.out.println(root);
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}

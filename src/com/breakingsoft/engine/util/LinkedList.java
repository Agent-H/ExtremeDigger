package com.breakingsoft.engine.util;


/**
 * Custom and simple linked list implementation.
 * insertion/removal : O(1)
 * traversal : O(n)
 * 
 * particularity : you need to keep a reference to the Entry if you want to remove it from the list
 * It is NOT designed to hold elements in a specific order, nor to perform researches ( O(n) )
 */
public class LinkedList<T> {
	
	private Entry mRoot;
	
	public LinkedList(){
		mRoot = new Entry();
	}
	
	public Entry first(){
		return mRoot.next();
	}
	
	public Entry insert(T data){
		return mRoot.insert(data);
	}
	
	public class Entry{
		private Entry mPrev, mNext;
		
		private T mData;
		
		public Entry(){
			
		}
		
		public Entry(T data){
			mData = data;
		}
		
		public T data(){
			return mData;
		}
		
		public Entry prev(){
			return mPrev;
		}
		
		public Entry next(){
			return mNext;
		}
		
		public void remove(){
			if(mNext != null){
				mNext.mPrev = mPrev;
			}
			if(mPrev != null){
				mPrev.mNext = mNext;
			}
		}
		
		public Entry insert(T data){
			
			Entry ent = new Entry(data);
			
			if(mNext != null){
				mNext.mPrev = ent;
			}
			ent.mNext = mNext;
			ent.mPrev = this;
			
			mNext = ent;
			
			return ent;
		}
	}
}

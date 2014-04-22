// @author A0097802Y

package butler.ui;

import javax.swing.text.*;

class TextFieldLengthChecker extends PlainDocument
{
	private int characterLength = 80;
	public TextFieldLengthChecker(){}

	public void insertString (int offset, String s, AttributeSet attributeSet) throws BadLocationException
	{
		try {
			if (offset < characterLength)
				super.insertString(offset, s, (javax.swing.text.AttributeSet) attributeSet);
		}
		catch (Exception e) {
		}
	}
} 

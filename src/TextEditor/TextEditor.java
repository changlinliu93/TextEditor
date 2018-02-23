package TextEditor;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class TextEditor extends JFrame implements ActionListener,KeyListener,MouseListener,ItemListener{
//menubar	
	JMenuBar bar=new JMenuBar();
	JMenu fileMenu=new JMenu("File");
		JMenuItem fileNew = new JMenuItem("New");
		JMenuItem fileOpen = new JMenuItem("Open");
		JMenuItem fileSave = new JMenuItem("Save");
		JMenuItem fileSaveAs = new JMenuItem("Save As");
		JMenuItem fileExit = new JMenuItem("Exit");
	JMenu editMenu = new JMenu("Edit");
		JMenuItem copyMenu = new JMenuItem("Copy");
		JMenuItem pasteMenu = new JMenuItem("Paste");
		JMenuItem cutMenu = new JMenuItem("Cut");
		JMenuItem selectMenu = new JMenuItem("Select");
	JMenu formatMenu = new JMenu("Format");
		JMenuItem colorMenu = new JMenuItem("Color");
		JMenuItem fontMenu = new JMenuItem("Font");
	
	JPanel pane =new JPanel(new BorderLayout());	
		JScrollPane scrollpane=new JScrollPane();
			JTextArea textarea=new JTextArea();
		JTextArea status=new JTextArea(1,10);
	
	JToolBar format=new JToolBar();
		JLabel font=new JLabel("Font ");
		JLabel size=new JLabel("Size ");
		JLabel color=new JLabel("Color ");
		JComboBox fontBox=new JComboBox();
		JComboBox colorBox=new JComboBox();
		JTextField sizetext=new JTextField(4);
		Button Italic=new Button("Italic");
		Button Bold=new Button("Bold");
	
	FileDialog openFD=new FileDialog(this,"Open",FileDialog.LOAD);
	FileDialog saveFD=new FileDialog(this,"Save",FileDialog.SAVE);
	
	JPopupMenu popmenu=new JPopupMenu();
		JMenuItem copyPop = new JMenuItem("Copy");
		JMenuItem pastePop = new JMenuItem("Paste");
		JMenuItem cutPop = new JMenuItem("Cut");
		
	
	BufferedReader in;
    FileReader read;
	BufferedWriter out;
	FileWriter writer;
	String fileName="NoName";
	String currentPath="";
	String currentFontName="";
	int currentFontStyle=0;
	int currentFontSize=0;
	
	
	Clipboard cb=new Clipboard("");
	

	
	public TextEditor(){
		super("TextEditor");		
		this.setJMenuBar(bar);
//File
		fileMenu.add(fileNew); 
		fileMenu.add(fileOpen);
		fileMenu.add(fileSave); 
		fileMenu.add(fileSaveAs);
		fileMenu.add(fileExit);
		bar.add(fileMenu);
		fileExit.addActionListener(this);
		fileOpen.addActionListener(this);
		fileSave.addActionListener(this);
		fileSaveAs.addActionListener(this);
		fileNew.addActionListener(this);
//Edit
		editMenu.add(copyMenu); editMenu.add(pasteMenu);
		editMenu.add(cutMenu); //editMenu.addSeparator();
		//editMenu.add(selectMenu);
		bar.add(editMenu);	
		copyMenu.addActionListener(this);
		pasteMenu.addActionListener(this);
		cutMenu.addActionListener(this);
		//selectMenu.addActionListener(this);
//Format
		formatMenu.add(colorMenu); formatMenu.add(fontMenu);
		//bar.add(formatMenu);	
		colorMenu.addActionListener(this);
		fontMenu.addActionListener(this);
//popmenu	
		popmenu.add(copyPop);
		popmenu.add(cutPop);
		popmenu.add(pastePop);
		copyPop.addActionListener(this);
		cutPop.addActionListener(this);
		pastePop.addActionListener(this);
		
//textarea
		textarea.setEditable(true); 
		textarea.addKeyListener(this);
		textarea.addMouseListener(this);
		currentFontName=textarea.getFont().getFontName();
		currentFontStyle=textarea.getFont().getStyle();
		currentFontSize=textarea.getFont().getSize();
		scrollpane.setViewportView(textarea);
		textarea.setText("在size框中输入你想要的大小(1~99)并按回车就可改变大小。如果你的字体太多了，点开字体下拉条会卡一下。另外请不要给中文设置英文字体。。。");
	
//formatbar
		sizetext.setText(Integer.toString(currentFontSize));
		sizetext.setEditable(true);
		sizetext.addKeyListener(this);
		fontBox.setRenderer(new FontBoxRenderer());
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		String fontName[]=ge.getAvailableFontFamilyNames();	
		JLabel fontLabel[]=new JLabel[fontName.length];	
		for(int i=0;i<fontName.length;i++) { 
			fontLabel[i]=new JLabel(fontName[i]);
			fontBox.addItem(fontLabel[i]);
		}
		fontBox.addItemListener(this);
		colorBox.setRenderer(new ColorBoxRenderer());
		colorBox.addItem(Color.BLACK);
		colorBox.addItem(Color.BLUE);
		colorBox.addItem(Color.CYAN);
		colorBox.addItem(Color.GREEN);
		colorBox.addItem(Color.RED);
		colorBox.addItem(Color.YELLOW);
		colorBox.addItemListener(this);
		Italic.setFont(new Font(currentFontName,Font.ITALIC,15));
		Bold.setFont(new Font(currentFontName,Font.BOLD,15));
		Italic.addMouseListener(this);
		Bold.addMouseListener(this);
		format.setLayout(new FlowLayout(FlowLayout.LEFT));
		format.add(font);
		format.add(fontBox);
		format.addSeparator();
		format.add(color);
		format.add(colorBox);
		format.addSeparator();
		format.add(size);
		format.add(sizetext);
		format.addSeparator();
		format.add(Italic);
		format.add(Bold);
//statusbar
		status.setText("The status bar is temporarily only for debugging use.");

		pane.add(scrollpane,BorderLayout.CENTER);
		pane.add(status,BorderLayout.SOUTH);
		pane.add(format,BorderLayout.NORTH);
		
		
		this.setContentPane(pane);	
		setBounds(200, 100, 650, 500);
		this.setVisible(true);
		
	}
			
	

//action
//file
	public void copy(){
		String s=textarea.getSelectedText();
        StringSelection select=new StringSelection(s);
        cb.setContents(select,null);
		
	}
	
	public void paste(){
		 String s="";
	        Transferable t = cb.getContents(null);
	        try {
	        	if (t != null&& t.isDataFlavorSupported(DataFlavor.stringFlavor)) {  
	                s = (String)t.getTransferData(DataFlavor.stringFlavor);
	            }
	        } 
	        catch (UnsupportedFlavorException ex) {ex.printStackTrace(); } 
	        catch (IOException ex) {ex.printStackTrace();}
	        textarea.insert(s,textarea.getCaretPosition());
	}
	
	public void cut(){
		String s=textarea.getSelectedText();
        StringSelection select=new StringSelection(s);
        cb.setContents(select,null);
        textarea.replaceRange("",textarea.getSelectionStart(),textarea.getSelectionEnd());
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==fileNew) {
	         textarea.setText("");
	       }
	      
		if(e.getSource()==fileOpen) {
	         openFD.show();
	         String s;
	         fileName=openFD.getDirectory()+openFD.getFile();
	         if(fileName!=null) {
	        	 try{
	        		 File file=new File(fileName);
	        		 read=new FileReader(file);
	        		 in=new BufferedReader(read);
	        		 textarea.setText("");
	        		 while((s=in.readLine())!=null) textarea.append(s+'\n');
	        		 in.close();
	        		 read.close();
	        	 	}
	            catch(IOException e2){}
	         }
	    }
		
		if(e.getSource()==fileSave){
			if(currentPath=="") e.setSource(fileSaveAs);
			else{
				try{				
					writer=new FileWriter(currentPath);
	        		out=new BufferedWriter(writer);
	        		out.write(textarea.getText(),0,(textarea.getText()).length());
	        		out.close();
	        		writer.close();
				}
				catch(IOException e2){};
				
			}
		}
	      
	    if(e.getSource()==fileSaveAs) {
	        saveFD.show();
	        currentPath=saveFD.getDirectory()+saveFD.getFile();
	        fileName=saveFD.getFile();
	        if(currentPath!=null) {      	
	        	try {	        		
	        		File file=new File(currentPath);
	        		writer=new FileWriter(file);
	        		out=new BufferedWriter(writer);
	        		out.write(textarea.getText(),0,(textarea.getText()).length());
	        		out.close();
	        		writer.close();
	        	}
	        catch(IOException e2){} 
	       }
	    }
	      
	    if(e.getSource()==fileExit) {    	
	        System.exit(0);
	    }
	    
//edit
	    if(e.getSource()==cutMenu||e.getSource()==cutPop) { cut();}
	    if(e.getSource()==copyMenu||e.getSource()==copyPop) { copy();}    
	    if(e.getSource()==pasteMenu||e.getSource()==pastePop) { paste();}
	    
//format
	    if(e.getSource()==fontMenu) {
	    	//textarea.setFont();
	    }
	}


//key
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		if(e.getSource()==textarea){			
			if(e.getModifiers()==InputEvent.CTRL_MASK&&e.getKeyCode()==KeyEvent.VK_C) copy();
			if(e.getModifiers()==InputEvent.CTRL_MASK&&e.getKeyCode()==KeyEvent.VK_V) paste();
			if(e.getModifiers()==InputEvent.CTRL_MASK&&e.getKeyCode()==KeyEvent.VK_X) cut();
		}
		if(e.getSource()==sizetext){
			if(e.getKeyChar()=='\n'){
				int size=Integer.parseInt(sizetext.getText());
				if(size>0&&size<100) {
				currentFontSize=size;
				Font f=new Font(currentFontName,currentFontStyle,currentFontSize);
				textarea.setFont(f);
				}	
			}
		}
	
	}


	public void mouseClicked(MouseEvent e) {
		if(e.getSource()==textarea&&e.getButton()==MouseEvent.BUTTON3) {
			int x=e.getX();
			int y=e.getY();
			popmenu.show(textarea, x, y);
		}
		if(e.getSource()==Italic&&e.getButton()==MouseEvent.BUTTON1) {
			if(currentFontStyle!=Font.ITALIC) currentFontStyle=Font.ITALIC;
			else currentFontStyle=Font.PLAIN;
			textarea.setFont(new Font(currentFontName,currentFontStyle,currentFontSize));
		}
		if(e.getSource()==Bold&&e.getButton()==MouseEvent.BUTTON1) {
			if(currentFontStyle!=Font.BOLD) currentFontStyle=Font.BOLD;
			else currentFontStyle=Font.PLAIN;
			textarea.setFont(new Font(currentFontName,currentFontStyle,currentFontSize));
		}
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource()==fontBox) {
			currentFontName=((JLabel)fontBox.getSelectedItem()).getText();
			Font f=new Font(currentFontName,currentFontStyle,currentFontSize);
			textarea.setFont(f);
		}
		if(e.getSource()==colorBox) {
			Color color=(Color)colorBox.getSelectedItem();
			textarea.setForeground(color);
		}
		
	}
	
	public static void main(String args[]){		
		TextEditor editor = new TextEditor();
		editor.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
	}
}




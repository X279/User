package com.amazonaws.samples.User;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog;
import java.util.List;

import javax.swing.*;

import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class PresentationLayer extends Thread implements  SQS{
	JFrame frame = new JFrame();
	JDialog newFrame = new JDialog();
	JButton searchButton = new JButton("search");
	JComboBox<String> beginyearBox = new JComboBox<String>();
	JComboBox<String> beginmonthBox = new JComboBox<String>();
	JComboBox<String> endyearBox = new JComboBox<String>();
	JComboBox<String> endmonthBox = new JComboBox<String>();
	JComboBox<String> searchMessBox = new JComboBox<String>();
	JLabel beginJLabel = new JLabel("begin:");
	JLabel endJLabel = new JLabel("end:");
	JLabel searchMessJLabel = new JLabel("search message:");
	GridBagLayout gridBagLayout = new GridBagLayout();//布局管理器
	GridBagConstraints constraints = null;//添加约束
	String[] strings = null;
	String beginyear = null;
	String beginmonth = null;
	String endyear = null;
	String endmonth = null;
	String searchMessage = null;
	TimeSeries timeSeries = null;
	JfreeChartPaint jfreeChartPaint = null;
	
	public void run()
	{
		frame.setSize(350, 350);
		frame.setTitle("表现层");
		frame.setLocationRelativeTo(null);
		frame.setLayout(gridBagLayout);
		place();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
	}
	
	public void handleData(String title)
	{
		this.timeSeries = new TimeSeries(title,org.jfree.data.time.Month.class);
		
		int by = Integer.parseInt(beginyear);
    	int bm = Integer.parseInt(beginmonth);
    	int ey = Integer.parseInt(endyear);
    	int em = Integer.parseInt(endmonth);
    	int curMonth = bm;int index=0;
    	for (int count = 0; count <= ey-by; count++) {
			for (int i = 0; i <= 12-curMonth; i++) {
				if (count==ey-by&&(curMonth+i)>em) {
					break;
				}
				timeSeries.add(new Month(curMonth+i,by+count),Double.parseDouble(strings[index]));
				index++;
			}
			curMonth=1;
		}
	}
	
	public void place()
	{
		int byear = 1999;
		for(int i = 0;i<=17;i++)
		{
			beginyearBox.addItem(String.valueOf(byear+i));
			endyearBox.addItem(String.valueOf(byear+i));
		}
		for(int i = 1;i<=12;i++)
		{
			if(i<10)
			{
				beginmonthBox.addItem("0"+String.valueOf(i));
				endmonthBox.addItem("0"+String.valueOf(i));
			}
			else {
				beginmonthBox.addItem(String.valueOf(i));
				endmonthBox.addItem(String.valueOf(i));
			}
		}
		searchMessBox.addItem("成交量(股)");
		searchMessBox.addItem("成交金额(元)");
		constraints = new GridBagConstraints();
		constraints.gridwidth = 3;
		constraints.weightx = 0;
		constraints.weighty = 0;
		gridBagLayout.addLayoutComponent(beginJLabel, constraints);
		frame.add(beginJLabel);
		constraints.gridwidth = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		gridBagLayout.addLayoutComponent(beginyearBox, constraints);
		frame.add(beginyearBox);
		constraints.gridwidth = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		gridBagLayout.addLayoutComponent(beginmonthBox, constraints);
		frame.add(beginmonthBox);
		constraints.gridwidth = 3;
		constraints.weightx = 0;
		constraints.weighty = 0;
		gridBagLayout.addLayoutComponent(endJLabel, constraints);
		frame.add(endJLabel);
		constraints.gridwidth = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		gridBagLayout.addLayoutComponent(endyearBox, constraints);
		frame.add(endyearBox);
		constraints.gridwidth = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		gridBagLayout.addLayoutComponent(endmonthBox, constraints);
		frame.add(endmonthBox);
		constraints.gridwidth = 3;
		constraints.weightx = 0;
		constraints.weighty = 0;
		gridBagLayout.addLayoutComponent(searchMessJLabel, constraints);
		frame.add(searchMessJLabel);
		constraints.gridwidth = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		gridBagLayout.addLayoutComponent(searchMessBox, constraints);
		frame.add(searchMessBox);
		constraints.gridwidth = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		gridBagLayout.addLayoutComponent(searchButton, constraints);
		searchButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("客户端：收到查询消息");
				beginyear = (String) beginyearBox.getSelectedItem();
				beginmonth = (String)beginmonthBox.getSelectedItem();

				endyear = (String)endyearBox.getSelectedItem();
				endmonth = (String)endmonthBox.getSelectedItem();
				
				searchMessage = (String)searchMessBox.getSelectedItem();
				System.out.println("客户端：开始发送消息");
				String message = beginyear + ";" +beginmonth+";"+endyear+ ";" + endmonth + ";" + searchMessage;
				SendMessageRequest send_msg_request = new SendMessageRequest()
			            .withQueueUrl(URL)
			            .withMessageBody(message)
			            .withDelaySeconds(0);
			    sqs.sendMessage(send_msg_request);
			    System.out.println("客户端：发送消息成功");
			    while(true) {
			    	try {
						Thread.currentThread();
						Thread.sleep(3000);
						System.out.println("客户端开始接收查询结果...");
						List<Message> messages = sqs.receiveMessage(URL1).getMessages();
						if(messages.size() > 0)
						{
							for(Message m : messages)
						    {
						    	String s = m.getBody();
						    	sqs.deleteMessage(URL1, m.getReceiptHandle());
						    	strings = s.split(";");
						    	String title = "某公司从" + beginyear + "/" + beginmonth + "到" + endyear + "/" + endmonth + "的" + searchMessage + "信息";
						    	handleData(searchMessage);
						    	jfreeChartPaint = new JfreeChartPaint(timeSeries,title,searchMessage);
						    	newFrame.add(jfreeChartPaint.getChartPanel());
						    	newFrame.setBounds(50, 50, 800, 600);
						    	newFrame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
						    	newFrame.setVisible(true);
						    }
							break;
						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			    }
			}
		});
		frame.add(searchButton);
	}
}

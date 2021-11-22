package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ConcurrGUI extends JFrame{
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    
   
    //costruttore
    public ConcurrGUI() {
        super();
        final Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screensize.getWidth() * WIDTH_PERC), (int) (screensize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        //add the buttons
        final JButton up = new JButton("up");
        final JButton down = new JButton("down");
        final JButton stop = new JButton("stop");
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        
        up.addActionListener(e -> agent.intCounting());
        down.addActionListener(e -> agent.decCounting());
        //una volta che schiacci stop non puoi più schiacciare gli altri tasti
        stop.addActionListener(e -> {
            agent.stopCounting(); 
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
           //il thread non andrebbe dentro il costruttore
        new Thread(agent).start();
    }
    
        //classe che ascolta quando l'utente schiaccia un bottone
        private class Agent implements Runnable {
            private volatile boolean up = true; //mettendo true sale sempre
            private volatile boolean stop;
            private volatile int counter;
            
            @Override
            public void run() {
                while(!this.stop) {
                    try {  
                        counter += up ? 1 : -1;
                        /*
                         * if (up == true) { counter ++; } 
                         * if (up == false) { counter --; }
                         */
                        
                        final var toDisplay = Integer.toString(counter); // far vedere sul display in maniera leggibile
                        
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            //cambio quello che c'è dentro label e lo rendo leggibile
                            public void run() {
                                display.setText(toDisplay);
                            }
                        });
                        //non lo incrementiamo perchè lo abbiamo già incrementato con intCounter
                        Thread.sleep(100);
                        }catch (InvocationTargetException | InterruptedException ex) {
                            /*
                             * This is just a stack trace print, in a real program there
                             * should be some logging and decent error reporting
                             */
                            ex.printStackTrace();
                        }
                    }
                
                
                
            }
            //bottone per incrementare e va dentro agent
            public void intCounting() {
                this.up = true;
                
            }
            
            //bottone per decrementare
            public void decCounting() {
                this.up = false;
                
            }
            
            //bottone per fermare
            public void stopCounting() {
                this.stop = true;
                
            }
        }
        
        
}

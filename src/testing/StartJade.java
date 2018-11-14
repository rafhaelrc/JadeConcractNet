package testing;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;

public class StartJade {
	
ContainerController cc;
    
    public static void main(String[] args) throws Exception {
        StartJade s = new StartJade();
        s.startContainer();
        s.createAgentsSellers(20);
        s.createAgentsBuyers(5,"book A");
    }

    void startContainer() {    	
        //Runtime rt= Runtime.instance();
        ProfileImpl p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        
        cc = Runtime.instance().createMainContainer(p);
    }
    
    void createAgentsSellers(int amount) throws Exception {
        for (int i=1; i <= amount; i++) {
            AgentController ac = cc.createNewAgent("seller"+i, "testing.BookSellerAgent", 
            									   new Object[] { i });
            ac.start();
        }
    }
    
    void createAgentsBuyers(int amount, String bookName) throws Exception {
        for (int i=1; i <= amount; i++) {
            AgentController ac = cc.createNewAgent("Buyer"+i, "testing.BookBuyerAgent", 
            									   new Object[] {bookName});
            ac.start();
        }
    }
}

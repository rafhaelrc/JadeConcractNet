package testing;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class BookSellerAgent  extends Agent{
	// The catalogue of books for sale (maps the title of a book to its price)
	 private Hashtable catalogue;
	 // The GUI by means of which the user can add books in the catalogue
	 //private BookSellerGui myGui;
	 
	 // Put agent initializations here
	 protected void setup(){
	 
		 // Create the catalogue
		 catalogue = new Hashtable();
	 
		 // Create and show the GUI
		 //myGui = new BookSellerGui(this);
		 //myGui.showGui();
	 
		 //List books = new ArrayList("a","b","c");
		 List books = new ArrayList(); 
		 books.add("book A");
		 books.add("book B");
		 books.add("book C");
		 books.add("book D");
		 
		 for(int i=0;i<books.size();i++){
			  System.out.println(books.get(i));
			  Random rand = new Random();
			  int randomNum = rand.nextInt((110 - 100) + 1) + 100;
			  catalogue.put(books.get(i), new Integer(randomNum));
			  System.out.println("Agent " +  getAID().getName() + 
					  			 " book is: " + books.get(i) + 
					  			 " price = " + randomNum);
		 }
		 
		 
		 /* Metodo usado para percorrer o hashmap
		  * Set<String> chaves = catalogue.keySet();
			for (String chave : chaves)
			{
				if(chave != null)
					System.out.println(chave + catalogue.get(chave));
			} 
		 System.exit(0); */
		 
		 // Register the book-selling service in the yellow pages
		 DFAgentDescription dfd = new DFAgentDescription();
		 dfd.setName(getAID());
		 ServiceDescription sd = new ServiceDescription();
		 sd.setType("book-selling");
		 sd.setName("JADE-book-trading");
		 dfd.addServices(sd);
		 try {
			 DFService.register(this, dfd);
		 }
		 catch (FIPAException fe) {
			 fe.printStackTrace();
		 }
	 
	 
	 // Add the behaviour serving requests for offer from buyer agents
	 addBehaviour(new OfferRequestsServer());
	 
	 // Add the behaviour serving purchase orders from buyer agents
	 addBehaviour(new PurchaseOrdersServer());
	 
	 } // finish method setup
	 
	// Put agent clean-up operations here
	 protected void takeDown() {
		// Deregister from the yellow pages
			try {
				DFService.deregister(this);
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
			// Close the GUI
		//myGui.dispose();
		 // Printout a dismissal message
		 System.out.println("Seller-agentSeller-agent "  +getAID().getName()+ " terminating.");
	}
	 
	 /*public void updateCatalogue(final String title, final int price) {
		 addBehaviour(new OneShotBehaviour() {
			 public void action() {
				 catalogue.put(title, new Integer(price));
				 System.out.println(title+" inserted into catalogue. Price = "+price);
			 }
		 });
	 }*/
	 
	 /**
	   Inner class OfferRequestsServer.
	   This is the behaviour used by Book-seller agents to serve incoming requests 
	   for offer from buyer agents.
	   If the requested book is in the local catalogue the seller agent replies 
	   with a PROPOSE message specifying the price. Otherwise a REFUSE message is
	   sent back.
	   
	   Note the use of the myAgent protected variable: each behavior has a pointer 
	   to the agent that is 
	   
	 */
	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				// Its search is verify if book is avaliable for sale.	
				Integer price = (Integer) catalogue.get(title);
				if (price != null) {
					// The requested book is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(price.intValue()));
				}
				else {
					// The requested book is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer
	
	/**
	   Inner class PurchaseOrdersServer.
	   This is the behaviour used by Book-seller agents to serve incoming 
	   offer acceptances (i.e. purchase orders) from buyer agents.
	   The seller agent removes the purchased book from its catalogue 
	   and replies with an INFORM message to notify the buyer that the
	   purchase has been sucesfully completed.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();

				Integer price = (Integer) catalogue.remove(title);
				if (price != null) {
					reply.setPerformative(ACLMessage.INFORM);
					System.out.println(title+" sold to agent "+msg.getSender().getName());
				}
				else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer 
	 
}

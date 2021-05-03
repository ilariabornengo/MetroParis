package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	Graph<Fermata, DefaultEdge> grafo ;
	
	Map<Fermata, Fermata> predecessore ;

	public void creaGrafo() {
		this.grafo = new SimpleGraph<>(DefaultEdge.class) ;
		
		MetroDAO dao = new MetroDAO() ;
		List<Fermata> fermate = dao.getAllFermate() ;
		
//		for(Fermata f : fermate) {
//			this.grafo.addVertex(f) ;
//		}
		
		Graphs.addAllVertices(this.grafo, fermate) ;
		
		// Aggiungiamo gli archi
		
//		for(Fermata f1: this.grafo.vertexSet()) {
//			for(Fermata f2: this.grafo.vertexSet()) {
//				if(!f1.equals(f2) && dao.fermateCollegate(f1, f2)) {
//					this.grafo.addEdge(f1, f2) ;
//				}
//			}
//		}
		
		List<Connessione> connessioni = dao.getAllConnessioni(fermate) ;
		for(Connessione c: connessioni) {
			this.grafo.addEdge(c.getStazP(), c.getStazA()) ;
		}
		
		System.out.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(), this.grafo.edgeSet().size()) ;
//		System.out.println(this.grafo) ;
		
//		Fermata f ;
		/*
		Set<DefaultEdge> archi = this.grafo.edgesOf(f) ;
		for(DefaultEdge e: archi) {
			/*
			Fermata f1 = this.grafo.getEdgeSource(e) ;
			// oppure
			Fermata f2 = this.grafo.getEdgeTarget(e) ;
			if(f1.equals(f)) {
				// f2 è quello che mi serve
			} else {
				// f1 è quello che mi serve
			}
			
			f1 = Graphs.getOppositeVertex(this.grafo, e, f) ;
		}*/
		
//		List<Fermata> fermateAdiacenti = Graphs.successorListOf(this.grafo, f) ;
//		Graphs.predecessorListOf(null, null)
	}
	
	public List<Fermata> fermateRaggiungibili(Fermata partenza) {
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = 
				new BreadthFirstIterator<>(this.grafo, partenza) ;
		this.predecessore=new HashMap<>();
		this.predecessore.put(partenza, null);
		
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>() {

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				DefaultEdge arco=e.getEdge();
				Fermata a=grafo.getEdgeSource(arco);
				Fermata b=grafo.getEdgeTarget(arco);
				//ho scoperto 'a' arrivando da 'b' 
				//(Se conoscevo gia b,ossia se è chiave della mappa)
				//se predecessore lo contiene allora quello è il vertice e
				//devo aggiungere nella mappa che a viene scoperto da b
				if(predecessore.containsKey(b) && !predecessore.containsKey(a))
				{
					predecessore.put(a, b);
					//System.out.println(a+ " scoperto da "+b);
					//a ha scoperto b
				}else if(predecessore.containsKey(a) &&!predecessore.containsKey(b))
				{
					//se conoscevamo già a allora 
					//ho scoperto b partendo da a
					predecessore.put(b, a);
					//System.out.println(b+ " scoperto da "+a);
				}
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				/*
				//System.out.println(e.getVertex());
				Fermata nuova=e.getVertex();
				//Fermata precedente=vertice adiacente a 'nuova' che sia già raggiunto;
				this.predecessore.put(nuova,precedente);
			*/}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}
		});
		
//		DepthFirstIterator<Fermata, DefaultEdge> dfv = 
//				new DepthFirstIterator<>(this.grafo, partenza) ;
		
		List<Fermata> result = new ArrayList<>() ;
		
		while(bfv.hasNext()) {
			Fermata f = bfv.next() ;
			result.add(f) ;
		}
		
		return result ;
	}
	
	public Fermata trovaFermata(String nome) {
		for(Fermata f: this.grafo.vertexSet()) {
			if(f.getNome().equals(nome)) {
				return f ;
			}
		}
		return null ;
	}
	
	public List<Fermata> trovaCammino(Fermata partenza,Fermata arrivo){
		fermateRaggiungibili(partenza);
		List<Fermata> result=new LinkedList<>();
		result.add(arrivo);
		//ci metto l'arrivo e poi vado indietro pian piano che trovo dei predecessori
		Fermata f=arrivo;
		//finchè il predecessore di f non è null
		//posso aggiungere a result il suo predecessore e
		//e spostare f nel suo predecessore
		while(predecessore.get(f)!=null)
		{
			f=predecessore.get(f);
			result.add(0,f);
		}
		//mi da una lista che contiene tutte le fermate dall'arrivo alla partenza
		//sul cammino minimo 
		//(al contrario
		return result;
	}
	
}
















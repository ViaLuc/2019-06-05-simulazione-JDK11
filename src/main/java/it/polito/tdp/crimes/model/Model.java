package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private List<Integer> vertici;
	private Graph<Integer, DefaultWeightedEdge> grafo;

	public Model() {
		this.dao = new EventsDao();
	}
		
	public List<Integer> getAnni(){
		List<Integer> anni = this.dao.getAnni();
		Collections.sort(anni);
		return anni;
	}
	
	public void creaGrafo(Integer anno) {
		vertici= this.dao.getVertici();
		grafo= new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, vertici);
		
		for(Integer v1: this.grafo.vertexSet())
		{
			for(Integer v2:this.grafo.vertexSet())
			{
				if(!v1.equals(v2))
				{
					if(this.grafo.getEdge(v1, v2)==null)
					{
						Double latMediav1 = dao.getLatMedia(anno,v1);
						Double latMediav2 = dao.getLatMedia(anno,v2);
						
						Double longMediav1 = dao.getlongMedia(anno,v1);
						Double longMediav2 = dao.getlongMedia(anno,v2);
						
						Double distanzaMedia = LatLngTool.distance(new LatLng(latMediav1, longMediav1),
																	new LatLng(latMediav2, longMediav2), LengthUnit.KILOMETER);
						Graphs.addEdgeWithVertices(this.grafo, v1, v2,distanzaMedia);
					}
				}
			}
		}
		
		System.out.println("GRAFO CREATO");
		System.out.println("#VERTICI" + this.grafo.vertexSet().size());
		System.out.println("#ARCHI" + this.grafo.edgeSet().size());
	}
	
	public List<Vicino> getVicini(Integer distretto){
		
		List<Vicino> vicini = new ArrayList<Vicino>();
		List<Integer> viciniId = Graphs.neighborListOf(this.grafo, distretto);
		
		for(Integer v : viciniId)
		{
			vicini.add(new Vicino(v,this.grafo.getEdgeWeight(this.grafo.getEdge(distretto, v))));
		}
		
		Collections.sort(vicini);
		
		return vicini;
		
	}

	public Set<Integer> getVertici() {
		return this.grafo.vertexSet();
	}
}

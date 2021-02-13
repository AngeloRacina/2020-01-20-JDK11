package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private SimpleWeightedGraph<Artist, DefaultWeightedEdge> grafo ;
	private ArtsmiaDAO dao;
	private Map<Integer, Artist> idMap;
	
	public Model() {
		this.dao = new ArtsmiaDAO();
		idMap = new HashMap<Integer, Artist>();
		dao.loadAllArtist(idMap);
	}
	
	public void creaGrafo(String ruolo) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//add vertices 
		Graphs.addAllVertices(grafo, dao.getArtists(ruolo));
	
		List<Adiacenza> adiacenze = dao.getAdiacenze(ruolo, idMap);
		for(Adiacenza a : adiacenze) {
			if(this.grafo.getEdge(a.getA1(), a.getA2()) == null)
				Graphs.addEdgeWithVertices(grafo, a.getA1(), a.getA2(), a.getPeso());
		}
	}
	
	public int nVertices() {
		return this.grafo.vertexSet().size();
	}
	
	public int nEdges() {
		return this.grafo.edgeSet().size();
	}
	
	public List<String> getRoles(){
		return dao.getRoles(); 
	}
	
	public List<Adiacenza> getEdges(String role){
		return this.dao.getAdiacenze(role, idMap);
	}
	
}

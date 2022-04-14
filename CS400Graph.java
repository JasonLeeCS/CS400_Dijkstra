// --== CS400 Project One File Header ==--
// Name: Jason Lee
// Email: jlee967@wisc.edu
// Team: blue
// Group: CI
// TA: Harper
// Lecturer: Florian
// Notes to Grader: <optional extra notes>
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.NoSuchElementException;

public class CS400Graph<T> implements GraphADT<T> 
{

    /**
     * Vertex objects group a data field with an adjacency list of weighted
     * directed edges that lead away from them.
     */
    protected class Vertex 
    {
      public T data; // vertex label or application specific data
      public LinkedList<Edge> edgesLeaving;

      public Vertex(T data) 
      {
          this.data = data;
          this.edgesLeaving = new LinkedList<>();
      }
    }

    /**
     * Edge objects are stored within their source vertex, and group together
     * their target destination vertex, along with an integer weight.
     */
    protected class Edge 
    {
      public Vertex target;
      public int weight;

      public Edge(Vertex target, int weight)
      {
          this.target = target;
          this.weight = weight;
      }
    }

    protected Hashtable<T, Vertex> vertices; // holds graph verticies, key=data
    public CS400Graph() { vertices = new Hashtable<>(); }

    /**
     * Insert a new vertex into the graph.
     * 
     * @param data the data item stored in the new vertex
     * @return true if the data can be inserted as a new vertex, false if it is 
     *     already in the graph
     * @throws NullPointerException if data is null
     */
    public boolean insertVertex(T data)
    {
      if(data == null) 
          throw new NullPointerException("Cannot add null vertex");
      if(vertices.containsKey(data)) return false; // duplicate values are not allowed
      vertices.put(data, new Vertex(data));
      return true;
    }
    
    /**
     * Remove a vertex from the graph.
     * Also removes all edges adjacent to the vertex from the graph (all edges 
     * that have the vertex as a source or a destination vertex).
     * 
     * @param data the data item stored in the vertex to remove
     * @return true if a vertex with *data* has been removed, false if it was not in the graph
     * @throws NullPointerException if data is null
     */
    public boolean removeVertex(T data)
    {
      if(data == null) throw new NullPointerException("Cannot remove null vertex");
      Vertex removeVertex = vertices.get(data);
      if(removeVertex == null) return false; // vertex not found within graph
      // search all vertices for edges targeting removeVertex 
      for(Vertex v : vertices.values())
      {
          Edge removeEdge = null;
          for(Edge e : v.edgesLeaving)
              if(e.target == removeVertex)
                  removeEdge = e;
          // and remove any such edges that are found
          if(removeEdge != null) v.edgesLeaving.remove(removeEdge);
      }
      // finally remove the vertex and all edges contained within it
      return vertices.remove(data) != null;
    }
    
    /**
     * Insert a new directed edge with a positive edge weight into the graph.
     * 
     * @param source the data item contained in the source vertex for the edge
     * @param target the data item contained in the target vertex for the edge
     * @param weight the weight for the edge (has to be a positive integer)
     * @return true if the edge could be inserted or its weight updated, false 
     *     if the edge with the same weight was already in the graph
     * @throws IllegalArgumentException if either source or target or both are not in the graph, 
     *     or if its weight is < 0
     * @throws NullPointerException if either source or target or both are null
     */
    public boolean insertEdge(T source, T target, int weight)
    {
      if(source == null || target == null) 
          throw new NullPointerException("Cannot add edge with null source or target");
      Vertex sourceVertex = this.vertices.get(source);
      Vertex targetVertex = this.vertices.get(target);
      if(sourceVertex == null || targetVertex == null) 
          throw new IllegalArgumentException("Cannot add edge with vertices that do not exist");
      if(weight < 0) 
          throw new IllegalArgumentException("Cannot add edge with negative weight");
      // handle cases where edge already exists between these verticies
      for(Edge e : sourceVertex.edgesLeaving)
          if(e.target == targetVertex)
          {
              if(e.weight == weight) return false; // edge already exists
              else e.weight = weight; // otherwise update weight of existing edge
              return true;
          }
      // otherwise add new edge to sourceVertex
      sourceVertex.edgesLeaving.add(new Edge(targetVertex,weight));
      return true;
    }    
    
    /**
     * Remove an edge from the graph.
     * 
     * @param source the data item contained in the source vertex for the edge
     * @param target the data item contained in the target vertex for the edge
     * @return true if the edge could be removed, false if it was not in the graph
     * @throws IllegalArgumentException if either source or target or both are not in the graph
     * @throws NullPointerException if either source or target or both are null
     */
    public boolean removeEdge(T source, T target)
    {
      if(source == null || target == null) throw new NullPointerException("Cannot remove edge with null source or target");
      Vertex sourceVertex = this.vertices.get(source);
      Vertex targetVertex = this.vertices.get(target);
      if(sourceVertex == null || targetVertex == null) throw new IllegalArgumentException("Cannot remove edge with vertices that do not exist");
      // find edge to remove
      Edge removeEdge = null;
      for(Edge e : sourceVertex.edgesLeaving)
          if(e.target == targetVertex) 
              removeEdge = e;
      if(removeEdge != null)
      { // remove edge that is successfully found                
          sourceVertex.edgesLeaving.remove(removeEdge);
          return true;
      }
      return false; // otherwise return false to indicate failure to find
    }
    
    /**
     * Check if the graph contains a vertex with data item *data*.
     * 
     * @param data the data item to check for
     * @return true if data item is stored in a vertex of the graph, false otherwise
     * @throws NullPointerException if *data* is null
     */
    public boolean containsVertex(T data) 
    {
      if(data == null) throw new NullPointerException("Cannot contain null data vertex");
      return vertices.containsKey(data);
    }
    
    /**
     * Check if edge is in the graph.
     * 
     * @param source the data item contained in the source vertex for the edge
     * @param target the data item contained in the target vertex for the edge
     * @return true if the edge is in the graph, false if it is not in the graph
     * @throws NullPointerException if either source or target or both are null
     */
    public boolean containsEdge(T source, T target) 
    {
      if(source == null || target == null) throw new NullPointerException("Cannot contain edge adjacent to null data"); 
      Vertex sourceVertex = vertices.get(source);
      Vertex targetVertex = vertices.get(target);
      if(sourceVertex == null) return false;
      for(Edge e : sourceVertex.edgesLeaving)
          if(e.target == targetVertex)
              return true;
      return false;
    }
    
    /**
     * Return the weight of an edge.
     * 
     * @param source the data item contained in the source vertex for the edge
     * @param target the data item contained in the target vertex for the edge
     * @return the weight of the edge (0 or positive integer)
     * @throws IllegalArgumentException if either sourceVertex or targetVertex or both are not in the graph
     * @throws NullPointerException if either sourceVertex or targetVertex or both are null
     * @throws NoSuchElementException if edge is not in the graph
     */
    public int getWeight(T source, T target) 
    {
      if(source == null || target == null) throw new NullPointerException("Cannot contain weighted edge adjacent to null data"); 
      Vertex sourceVertex = vertices.get(source);
      Vertex targetVertex = vertices.get(target);
      if(sourceVertex == null || targetVertex == null) throw new IllegalArgumentException("Cannot retrieve weight of edge between vertices that do not exist");
      for(Edge e : sourceVertex.edgesLeaving)
          if(e.target == targetVertex)
              return e.weight;
      throw new NoSuchElementException("No directed edge found between these vertices");
    }
    
    /**
     * Return the number of edges in the graph.
     * 
     * @return the number of edges in the graph
     */
    public int getEdgeCount()
    {
      int edgeCount = 0;
      for(Vertex v : vertices.values())
          edgeCount += v.edgesLeaving.size();
      return edgeCount;
    }
    
    /**
     * Return the number of vertices in the graph
     * 
     * @return the number of vertices in the graph
     */
    public int getVertexCount()
    {
      return vertices.size();
    }

    /**
     * Check if the graph is empty (does not contain any vertices or edges).
     * 
     * @return true if the graph does not contain any vertices or edges, false otherwise
     */
    public boolean isEmpty() 
    {
      return vertices.size() == 0;
    }

    /**
     * Path objects store a discovered path of vertices and the overal distance of cost
     * of the weighted directed edges along this path. Path objects can be copied and extended
     * to include new edges and verticies using the extend constructor. In comparison to a
     * predecessor table which is sometimes used to implement Dijkstra's algorithm, this
     * eliminates the need for tracing paths backwards from the destination vertex to the
     * starting vertex at the end of the algorithm.
     */
    protected class Path implements Comparable<Path> 
    {
      public Vertex start; // first vertex within path
      public int distance; // sumed weight of all edges in path
      public List<T> dataSequence; // ordered sequence of data from vertices in path
      public Vertex end; // last vertex within path

        /**
         * Creates a new path containing a single vertex.  Since this vertex is both
         * the start and end of the path, it's initial distance is zero.
         * @param start is the first vertex on this path
         */
        public Path(Vertex start)
        {
          this.start = start;
          this.distance = 0;
          this.dataSequence = new LinkedList<>();
          this.dataSequence.add(start.data);
          this.end = start;
        }

        /**
         * This extension constructor makes a copy of the path passed into it as an argument
         * without affecting the original path object (copyPath). The path is then extended
         * by the Edge object extendBy.
         * @param copyPath is the path that is being copied
         * @param extendBy is the edge the copied path is extended by
         */
        public Path(Path copyPath, Edge extendBy) 
        {
          this.start = copyPath.start;
          this.distance = copyPath.distance + extendBy.weight; // Adds weight on to distance
          this.dataSequence = new LinkedList <>(); // Creates an new independent linked list
          
          for(T data : copyPath.dataSequence) // This loop copies the data values over
          {
            if(!this.dataSequence.contains(data))
            {
              this.dataSequence.add(data);
            }
          }
          
          this.dataSequence.add(extendBy.target.data); // Adds the last data value
          this.end = extendBy.target;
        }

        /**
         * Allows the natural ordering of paths to be increasing with path distance.
         * When path distance is equal, the string comparison of end vertex data is used to break ties.
         * @param other is the other path that is being compared to this one
         * @return -1 when this path has a smaller distance than the other,
         *         +1 when this path has a larger distance that the other,
         *         and the comparison of end vertex data in string form when these distances are tied
         */
        public int compareTo(Path other) 
        {
          int cmp = this.distance - other.distance;
          if(cmp != 0) return cmp; // use path distance as the natural ordering
          // when path distances are equal, break ties by comparing the string
          // representation of data in the end vertex of each path
          return this.end.data.toString().compareTo(other.end.data.toString());
        }
    }

    /**
     * Uses Dijkstra's shortest path algorithm to find and return the shortest path 
     * between two vertices in this graph: start and end. This path contains an ordered list
     * of the data within each node on this path, and also the distance or cost of all edges
     * that are a part of this path.
     * @param start data item within first node in path
     * @param end data item within last node in path
     * @return the shortest path from start to end, as computed by Dijkstra's algorithm
     * @throws NoSuchElementException when no path from start to end can be found,
     *     including when no vertex containing start or end can be found
     */
    protected Path dijkstrasShortestPath(T start, T end) throws NoSuchElementException
    {   
      if(!this.containsVertex(start)|| !this.containsVertex(end))
      {
        throw new NoSuchElementException("There are no vertices that contain start/end values");
      }
      
      PriorityQueue <Path> pQueue = new PriorityQueue <Path> ();
      Path firstPath = new Path(new Vertex(start)); // This is the first possible path
      Vertex last = this.vertices.get(end); // This is the ending vertex
      Vertex current = this.vertices.get(start); // This is the starting vertex
      
      Hashtable <Vertex, Integer> distances = new Hashtable <Vertex, Integer>(); // Records the shortest distance from starting vertex
      
      for(Edge e : current.edgesLeaving)
      {
        pQueue.add(new Path(firstPath ,e));
      }
      
      Path shortestPath = firstPath; // This stores shortest path
      distances.put(current,  firstPath.distance); // This marks the starting vertex as "visited"
      
      while(!pQueue.isEmpty() && current != last) // This will iterate through pQueue
      {
        Path currentPath = pQueue.peek();
        current = pQueue.poll().end; // This pops the head of the pQueue
        
        if(distances.containsKey(current)) // This will skip iteration if we are on a "visited" vertex
        {
          continue;
        }
        
        if(current != last) // Update pQueue if the current vertex is not the end vertex
        {
          for(Edge e : current.edgesLeaving) 
          {
            pQueue.add(new Path(currentPath, e));
          }
        }
        
        shortestPath = currentPath; // Updates the shortest path
        distances.put(current,  shortestPath.distance); // Adds the current vertex and it's distance to the hashtable 
      }
      
      if(!distances.containsKey(last))
      {
        throw new NoSuchElementException("A path cannot be found between the given values");
      }
      
      return shortestPath; 
    }
    
    /**
     * Returns the shortest path between start and end.
     * Uses Dijkstra's shortest path algorithm to find the shortest path.
     * 
     * @param start the data item in the starting vertex for the path
     * @param end the data item in the destination vertex for the path
     * @return list of data item in vertices in order on the shortest path between vertex 
     * with data item start and vertex with data item end, including both start and end 
     * @throws NoSuchElementException when no path from start to end can be found
     *     including when no vertex containing start or end can be found
     */
    public List<T> shortestPath(T start, T end) 
    {
      return dijkstrasShortestPath(start,end).dataSequence;
    }
    
    /**
     * Returns the cost of the path (sum over edge weights) between start and end.
     * Uses Dijkstra's shortest path algorithm to find the shortest path.
     * 
     * @param start the data item in the starting vertex for the path
     * @param end the data item in the end vertex for the path
     * @return the cost of the shortest path between vertex with data item start 
     * and vertex with data item end, including all edges between start and end
     * @throws NoSuchElementException when no path from start to end can be found
     *     including when no vertex containing start or end can be found
     */
    public int getPathCost(T start, T end)
    {
      return dijkstrasShortestPath(start, end).distance;
    }   
    
}
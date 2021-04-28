/***********************************************************************
 * cs3524.solutions.mud.Vertex
 ***********************************************************************/

package mud;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

// Represents a location in the MUD (a vertex in the graph).
class Vertex
{
    public String _name;             // Vertex name
    public String _msg = "";         // Message about this location
    public Map<String,Edge> _routes; // Association between direction
				     // (e.g. "north") and a path
				     // (Edge)
    public List<String> _things;     // The things  at
				     // this location
		public List<String> _players; //players

    public Vertex( String nm )
    {
			_name = nm; 
			_routes = new HashMap<String,Edge>(); // Not synchronised
			_things = new Vector<String>();       // Synchronised
			_players = new Vector<String>(); // synchronised
    }

    public String toString()
    {
			String summary = "\n";
			summary += _msg + "\n";
			Iterator iter = _routes.keySet().iterator();
			String direction;
			while (iter.hasNext()) {
					direction = (String)iter.next();
					summary += "To the " + direction + " there is " + ((Edge)_routes.get( direction ))._view + "\n";
			}
			return summary;
    }

    public String showPlayers() {
    	String summary = "Players you can see: ";
			for (String player : _players) 
			{ 
				summary += player + " ";
			}
    	return summary;
		}

		public String showItems() {
			Iterator iter = _things.iterator();
			String summary = "Things you can see: ";
			if (iter.hasNext()) {
				do {
					summary += iter.next() + " ";
					} while (iter.hasNext());
				}
			return summary;
		}

}


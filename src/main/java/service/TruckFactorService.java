package service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/truckFactorService")
@Produces(MediaType.APPLICATION_JSON)
public class TruckFactorService {
	
	@GET
	public int getTruckFactor(@QueryParam("projectName") String projectName) {
		return 0;
	}
	
	@GET
	@Path("/truckFactorDao")
	public int getTruckFactorDOA(@QueryParam("projectName") String projectName) {
		return 0;
	}
	
	@GET
	@Path("/truckFactorDoe")
	public int getTruckFactorDOE(@QueryParam("projectName") String projectName) {
		return 0;
	}
}

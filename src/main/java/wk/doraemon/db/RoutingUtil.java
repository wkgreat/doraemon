package wk.doraemon.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by 1001225 on 2019/8/16.
 */
public class RoutingUtil {

    public String roadTN = "road_network";
    public String verticesTN = "road_network_vertices_pgr";
    public final static double INF = Double.MAX_VALUE/2 - 1;

    public static Map<Integer,Map<Integer,Double>> roadCostMap = null;

    public double getCost(int sourceId, int targetId) {

        Connection ct = PostGISUtil.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        double cost = INF;
        String sql =
                "select min(length) from "+roadTN+" where (source=? and target=?) or (target=? and source=?)";
        try {
            ps = ct.prepareStatement(sql);
            ps.setInt(1,sourceId);
            ps.setInt(2,targetId);
            ps.setInt(3,sourceId);
            ps.setInt(4,targetId);
            rs = ps.executeQuery();
            if(rs.next()) {
                cost = rs.getDouble(1);
            }
            return cost;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PostGISUtil.close(rs,ps);
        }
        return cost;
    }

    public double getDirectCost(int sourceId, int targetId) {
        if(roadCostMap==null) {
            roadCostMap = getRoadCostMap();
        }
        Map<Integer,Double> edges = roadCostMap.getOrDefault(sourceId,null);
        if(edges==null) {
            return INF;
        }
        return edges.getOrDefault(targetId,INF);
    }

    public double getNoDirectCost(int sourceId, int targetId) {
        return Math.min(getDirectCost(sourceId,targetId),getDirectCost(targetId,sourceId));
    }

    public Map<Integer,Map<Integer,Double>> getRoadCostMap() {

        Connection ct = PostGISUtil.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Integer,Map<Integer,Double>> roadCostMap = new HashMap<>();
        String sql =
                "select source, target, length from "+roadTN;
        try {
            ps = ct.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                int source = rs.getInt(1);
                int target= rs.getInt(2);
                double cost = rs.getDouble(3);
                Map<Integer, Double> edges = roadCostMap.getOrDefault(source,new HashMap<>());
                edges.put(target,cost);
                roadCostMap.put(source,edges);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PostGISUtil.close(rs,ps);
        }
        return roadCostMap;
    }

    public int getVetciesSize() {
        Connection ct = PostGISUtil.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int theSize = Integer.MAX_VALUE;
        String sql =
                "select count(1) from "+verticesTN;
        try {
            ps = ct.prepareStatement(sql);
            rs = ps.executeQuery();
            if(rs.next()) {
                theSize = rs.getInt(1);
            }
            return theSize;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PostGISUtil.close(rs,ps);
        }
        return theSize;
    }

    public List<Integer> dijkstra(int sourceId, int targetId) {

        int theSize = getVetciesSize();

        int[] path = new int[theSize+1];
        double[] dist = new double[theSize+1];
        boolean[] set = new boolean[theSize+1];

        set[sourceId] = true;
        path[sourceId] = -1;
        for(int i=1; i<=theSize; ++i) {
            if(!set[i]) {
                double cost = getNoDirectCost(sourceId,i);
                if(cost<INF-1) {
                    path[i] = sourceId;
                    dist[i] = cost;
                } else {
                    dist[i] = INF;
                }
            }
        }

        for(int i=2;i<=theSize; ++i) {
            int minIndex = findMinIndex(theSize,set,dist);
            set[minIndex]=true;
            refreshGraph(minIndex,theSize,set,dist,path);

        }

        List<Integer> route = new ArrayList<>();
        if(dist[targetId] >= INF) {
            return route;
        }
        int pid = targetId;
        while(pid>0) {
            route.add(pid);
            pid = path[pid];
        }
        Collections.reverse(route);
        return route;
    }

    void refreshGraph(int minIndex, int theSize, boolean[] set, double[] dist, int[] path) {
        for(int i=1; i<=theSize; ++i) {
            if(!set[i]) {
                double theDist = dist[minIndex] + getNoDirectCost(minIndex,i);
                theDist = Math.min(theDist,INF);
                if(theDist<dist[i]) {
                    dist[i] = theDist;
                    path[i] = minIndex;
                }
            }
        }
    }

    int findMinIndex(int theSize, boolean[] set, double[] dist) {
        double minDist = INF;
        int minIndex = -1;
        for(int i=1; i<=theSize; ++i) {
            if(!set[i]) {
                if(dist[i]<=minDist) {
                    minDist = dist[i];
                    minIndex = i;
                }
            }
        }
        return minIndex;
    }

    public static void main(String[] args) {

        RoutingUtil routingUtil = new RoutingUtil();
        List<Integer> route = routingUtil.dijkstra(1,300);
        for(int r : route) {
            System.out.println(r);
        }

    }

}

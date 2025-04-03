/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package distsys.week6_lab;

import grpc.generated.PatientsRoomControl.*;
import grpc.generated.PatientsRoomControl.PatientsRoomControlGrpc.PatientsRoomControlImplBase;


import io.grpc.Server;
import io.grpc.ServerBuilder; //this is from the stub, to generate an instance of StreamObserver
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import java.util.logging.Logger; 
/**
 *
 * @author marti
 */
public class PRCServer extends PatientsRoomControlImplBase {
    private static final Logger logger = Logger.getLogger(PRCServer.class.getName());

	public static void main(String[] args) {
		
          
		PRCServer Serviceserver = new PRCServer();
		
		int port = 50051;
	    
		try {
			Server server = ServerBuilder.forPort(port)
			    .addService(Serviceserver)
			    .build()
			    .start();
			 logger.info("Server started, listening on " + port);
                         
	         System.out.println(" Server started, listening on " + port);		   
			 server.awaitTermination();

			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	   
	}
        
    @Override  
    public void lightControl(LightRequest request, StreamObserver<LightResponse> responseObserver){
        
        String reg = String.valueOf(request.getLightRequest()) ;
       
        LightResponse lightResponse = LightResponse.newBuilder().setLightResponse(reg).build();
        
        responseObserver.onNext(lightResponse);
        responseObserver.onCompleted();
    
    }
    
    @Override
    public void bedHeight(HeightRequest request, StreamObserver<HeightResponse> response){
        
        String reg = String.valueOf(request.getHeightRequest()) ;
       
        HeightResponse heightResponse = HeightResponse.newBuilder().setHeightResponse(reg).build();
        
        response.onNext(heightResponse);
        response.onCompleted();    
        
        
    }
    
    @Override
    public void courtainsGap(GapRequest request, StreamObserver<GapResponse> response){
        
        String reg = String.valueOf(request.getCourtainsRequest()) ;
       
        GapResponse gapResponse = GapResponse.newBuilder().setCourtainsResponse(reg).build();
        
        response.onNext(gapResponse);
        response.onCompleted();    
        
    }
        
        
        
        
        
        
        
        
        
        
}

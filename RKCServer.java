/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package distsys.week6_lab;

import grpc.generated.RoomKeyControls.*;
import grpc.generated.RoomKeyControls.RoomKeyControlsGrpc.RoomKeyControlsImplBase;

import io.grpc.Server;
import io.grpc.ServerBuilder; //this is from the stub, to generate an instance of StreamObserver
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.LocalTime;

import java.util.logging.Logger; 

/**
 *
 * @author marti
 */
public class RKCServer extends RoomKeyControlsImplBase {
    
    private static final Logger logger = Logger.getLogger(RKCServer.class.getName());
    private HospitalGUI gui;
    
    public RKCServer(HospitalGUI gui){
        this.gui = gui;
    }

    public static void main(String[] args) {
            
            HospitalGUI gui = new HospitalGUI();
            gui.setVisible(true);
            
            RKCServer Serviceserver = new RKCServer(gui);

            int port = 50052;

            try {
                    Server server = ServerBuilder.forPort(port)
                        .addService(Serviceserver)
                        .build()
                        .start();
                     logger.info("Server started, listening on " + port);
                     
                     String message = "Server started, listening on " + port;
                     //System.out.print(message);
                     gui.appendRKCServerText(message);
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
    public StreamObserver<RoomRequest> roomControlConditions(StreamObserver<RoomConditions> responseObserver) {


        return new StreamObserver<RoomRequest>() {

    
            RoomsSettings room = new RoomsSettings();

            @Override
           
            public void onNext(RoomRequest request) {

                String message = (LocalTime.now().toString() + ": received a message: " + request.getRoomName());
                gui.appendRKCServerText(message);
               
                int [] roomsValue = (int []) room.getRoomValues(request.getRoomName());
                
                RoomConditions reply = RoomConditions.newBuilder()
                                        .setTemp(roomsValue[0])
                                        .setHumidity(1)
                                        .build();
                
                
                responseObserver.onNext(reply);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.printf(LocalTime.now().toString() + ": Message stream complete \n");
                     responseObserver.onCompleted();
            }

        }; 
        
    }

   
    

    
    
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}









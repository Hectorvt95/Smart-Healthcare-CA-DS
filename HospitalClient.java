/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package distsys.week6_lab;

import java.util.concurrent.CountDownLatch; //this is to slow the client until the server stream is done
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import grpc.generated.PatientsRoomControl.PatientsRoomControlGrpc;
import grpc.generated.PatientsRoomControl.PatientsRoomControlGrpc.PatientsRoomControlBlockingStub;
import grpc.generated.PatientsRoomControl.*;

import grpc.generated.RoomKeyControls.RoomKeyControlsGrpc;
import grpc.generated.RoomKeyControls.RoomKeyControlsGrpc.RoomKeyControlsStub;
import grpc.generated.RoomKeyControls.*;

import grpc.generated.SmartMonitor.SmartMonitorGrpc;
import grpc.generated.SmartMonitor.SmartMonitorGrpc.SmartMonitorStub;
import grpc.generated.SmartMonitor.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver; //this is from the stub, to generate an instance of StreamObserver

import java.time.LocalTime;
import java.util.Iterator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author marti
 */
public class HospitalClient {
    
    //Lets create the observer that is going to be looking after the different server stream methods
    private StreamObserver<RoomRequest> roomRequestObserver;

    //a blocking stub to make a synchronus call --> PatientsRoomControl - Unary
    private static PatientsRoomControlBlockingStub prc_syncStub;

    //a non blocking stub to make an asynchronus call --> RoomKeyControls - BI DI stream
    private static RoomKeyControlsStub rkc_asyncStub;

    //a non blocking stub to make an ascynchronus call --> SmartMonitor - Client stream
    public static SmartMonitorStub sm_asyncStub;
    
    //lets initialize our hospital client class
    public HospitalClient(){
        
        String host = "localhost";
        
        //STUB FOR PATIENTS ROOM CONTROL
        int prcserver = 50051;
        ManagedChannel channelPRC = ManagedChannelBuilder.
                        forAddress(host, prcserver)
                        .usePlaintext()
                        .build();
        prc_syncStub = PatientsRoomControlGrpc.newBlockingStub(channelPRC); //PRC
        
        //STUB FOR ROOM KEY CONTROLS
        int rkcserver = 50052;
        ManagedChannel channelRKC = ManagedChannelBuilder.
                        forAddress(host, rkcserver)
                        .usePlaintext()
                        .build();
        rkc_asyncStub = RoomKeyControlsGrpc.newStub(channelRKC); //RKC
        
        //STUB FOR SMART MONITOR
        int smserver = 50053;
        ManagedChannel channelSM = ManagedChannelBuilder.
                        forAddress(host, smserver)
                        .usePlaintext()
                        .build();
        sm_asyncStub = SmartMonitorGrpc.newStub(channelSM); //SM
    }

  
    public static void main(String [] args) throws InterruptedException {
        
        HospitalClient hospitalClient = new HospitalClient();
        
        //requestPRC_lightControl();
        //requestPRC_bedHeight();
        //requestPRC_courtainsGap();
        
        
        //hospitalClient.requestRKC_values("Hector");
        
        // sendPatientData();


    }
    
    //** UNARY - PATIENTS ROOM CONTROL
    public void requestPRC_lightControl(JTextArea textArea, int value){
        SwingUtilities.invokeLater(() -> textArea.append("Unary - PatientRoomControl - Lights Control."));
        
        LightRequest request = LightRequest.newBuilder()
                .setLightRequest(value)    //this is going to be taken from the gui
                .build();
        
        LightResponse response =  prc_syncStub.lightControl(request);   
        SwingUtilities.invokeLater(() -> textArea.append("The value of the light been dimmed to " + value + "%." + "\n"));
        //System.out.println("The value of the light been dimmed to " + response.getLightResponse() + "%.");
    }
    
    
    public void requestPRC_bedHeight(JTextArea textArea, int value){
        System.out.println("Unary - PatientRoomControl - Height Control.");
        HeightRequest request = HeightRequest.newBuilder()
                .setHeightRequest(value)  //this is going to be taken from the gui
                .build();
        HeightResponse response = prc_syncStub.bedHeight(request);
        SwingUtilities.invokeLater(() -> textArea.append("The value of the height is at its " + value + "%." + "\n"));
        //System.out.println("The value of the height is at its " + response.getHeightResponse() + "%.");
    }
    
    
    public void requestPRC_courtainsGap(JTextArea textArea, int value){
        System.out.println("Unary - PatientRoomControl - Courtains Gap.");
        GapRequest request = GapRequest.newBuilder()
                .setCourtainsRequest(value)  //this is going to be taken from the gui
                .build();
        GapResponse response = prc_syncStub.courtainsGap(request);
        SwingUtilities.invokeLater(() -> textArea.append("The courtains are " + value + "% open." + "\n"));
        //System.out.println("The courtains are " + response.getCourtainsResponse() + "% open." );
    }
    
    
    
    
    
    
    //** BI DI - ROOM KEY CONTROL
    public void requestRKC_values(String roomName, JTextArea textArea){
        System.out.println("Bi Di Streaming - Room Key Control - Values for all of the Room Settings");
        StreamObserver<RoomConditions> responseObserver = new StreamObserver<RoomConditions>() {
            
            @Override
            public void onNext(RoomConditions response){
               
               // validateRoomsData(response);
               String sTemp = String.valueOf(response.getTemp());
               String sHum = String.valueOf(response.getHumidity());
               String message = (LocalTime.now().toString() + ": response from server : " + "Temperature: " + sHum + ", " + "Humidity: " + sTemp + "\n");
               SwingUtilities.invokeLater(() -> textArea.append(message + "\n")); // setText(message));
            }
            
            @Override
            public void onError(Throwable t){
                t.printStackTrace();
            }
            
            @Override
            public void onCompleted(){
                SwingUtilities.invokeLater(() -> textArea.append(LocalTime.now().toString() + ": stream is completed." + "\n"));
                System.out.println(LocalTime.now().toString() + ": stream is completed.");
            }
            
            
        };
        
        StreamObserver<RoomRequest> requestObserver =  rkc_asyncStub.roomControlConditions(responseObserver);
        
        try{
            
            String [] rooms = {"neonatal unit", "microbiology unit", "hematology unit", "molecular diagnostics unit", 
                                "sterile storage unit", "operating theater unit", "room 1", "room 2", "room 3", "pharmaceutical storage unit"};
           
            
            if(contains(rooms,roomName)){
                
                requestObserver.onNext(RoomRequest.newBuilder().setRoomName(roomName.toLowerCase()).build());  //this is going to be taken from the gui
                SwingUtilities.invokeLater(() -> textArea.append("Client called the server for the Neonatal Intensive Care Unit Values \n"));
                System.out.println("Client called the server for the Neonatal Intensive Care Unit Values");
                Thread.sleep(500);
                
            }else{
                SwingUtilities.invokeLater(() -> textArea.append("No Such Room Exception: Unexistent Room \n"));
            }
           
            //create a botton to throw an onCompleted(), so i can finish the service of Bidirectional
            
              /**
            requestObserver.onNext(RoomRequest.newBuilder().setRoomName("Neonatal Unit").build());  //this is going to be taken from the gui
            System.out.println("Client called the server for the Neonatal Intensive Care Unit Values");
            Thread.sleep(500);
            
            requestObserver.onNext(RoomRequest.newBuilder().setRoomName("Microbiology Unit").build()); //this is going to be taken from the gui
            System.out.println("Client called the server for the Microbiology Unit Values");
            Thread.sleep(500);
            
            requestObserver.onNext(RoomRequest.newBuilder().setRoomName("Hematology Unit").build()); //this is going to be taken from the gui
            System.out.println("Client called the server for the Hematology Unit Values");
            Thread.sleep(500);
            
            requestObserver.onNext(RoomRequest.newBuilder().setRoomName("Molecular Diagnostics Unit").build()); //this is going to be taken from the gui
            System.out.println("Client called the server for the Molecular Diagnostics Values");
            Thread.sleep(500);
            
            requestObserver.onNext(RoomRequest.newBuilder().setRoomName("Operating Theater Unit").build()); //this is going to be taken from the gui
            System.out.println("Client called the server for the Operating Theater Values");
            Thread.sleep(500);
             
            requestObserver.onNext(RoomRequest.newBuilder().setRoomName("Room 1").build()); //this is going to be taken from the gui
            System.out.println("Client called the server for the Room 1 Values");
            Thread.sleep(500);
            
            requestObserver.onNext(RoomRequest.newBuilder().setRoomName("Room 2").build()); //this is going to be taken from the gui
            System.out.println("Client called the server for the Room 2 Values");
            Thread.sleep(500);
            
            requestObserver.onNext(RoomRequest.newBuilder().setRoomName("Room 3").build()); //this is going to be taken from the gui
            System.out.println("Client called the server for the Room 3 Values");
            Thread.sleep(500);
             
            **/
            
        }catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e);
        }
     
    }
    public void completeStream(){
        roomRequestObserver.onCompleted();
    }
    
    public static Boolean contains(String [] rooms, String roomName){
        int n = 0;
        while(n < rooms.length){
            if(rooms[n].equals(roomName)){
                return true;
            }
            n++;
        }
        return false;
    }
    
    private static void validateRoomsData(RoomConditions room) {
        // Example validation logic
        if (room.getTemp() < 18) {
            System.out.println("Abnormal temperature detected. Alerting response team.");
        }   
        else if(room.getHumidity()> 60){
            System.out.println("Abnormal humidity detected. Alerting response team.");
        } 
         else {
            System.out.println("Normal readings.");
        }
    }
    
    
    //Client Streaming - SMART MONITOR
    public static void sendPatientData() throws InterruptedException{
    
        System.out.println("Client Streaming - Smart tMonitor");
        
        StreamObserver<Validation> responseObserver = new StreamObserver<Validation>(){
            
            @Override
            public void onNext(Validation response){
              System.out.println(LocalTime.now().toString() + ": response from server :" + response.getMessage());
              
            }
            
            @Override
            public void onError(Throwable t){
                t.printStackTrace();
            }
            
            @Override
            public void onCompleted(){
                System.out.println(LocalTime.now().toString() + ": Stream is completed");
            }
        
        };
        
        StreamObserver<PatientData> requestObserver = sm_asyncStub.smartPatientMonitor(responseObserver);
        
        try{
            //sendSampleReadings(requestObserver);
   
            
            PatientData.Builder patient1 = PatientData.newBuilder();
            
            requestObserver.onNext(patient1.setPatientName("John Johns").build());
            Thread.sleep(500);
            
            requestObserver.onNext(patient1.setRoomNumber(15).build());
            Thread.sleep(500);
            
            requestObserver.onNext(patient1.setTemperature(300000).build());
            Thread.sleep(500);
            
            requestObserver.onNext(patient1.setBloodPressure(400000).build());
            Thread.sleep(500);
            
            requestObserver.onNext(patient1.setElectRate(5000000).build());
            Thread.sleep(500);
            
            requestObserver.onCompleted();
            Thread.sleep(500);
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            
        }
    
    }
    
    private static void sendSampleReadings(StreamObserver<PatientData> requestObserver) {
        try {
            // First patient
            PatientData patient1 = PatientData.newBuilder()
                .setRoomNumber(101)
                .setPatientName("John Doe")
                .setTemperature(37)
                .setBloodPressure(120)
                .setElectRate(80)
                .build();
            requestObserver.onCompleted();
            

        } catch (Exception e) {
            requestObserver.onError(e);
            throw e;
        }
    }
    
    
    //Server Streaming - ?????
    
 
}



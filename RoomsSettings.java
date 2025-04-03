/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package distsys.week6_lab;

//https://www.hpsc.ie/a-z/environmentandhealth/severeweatherevents/heat/heathealthadviceforhealthandcareprofessionals/
//this link was used as source of guidance to determine the range of the temperature and humidity in irish hospitals.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marti
 */
public class RoomsSettings {
    
    Map Rooms;
   
     
    public class RoomValues{
        
        private int temperature;
        private int humidity;
        
        public RoomValues(int temp, int hum){
            this.temperature = temp;
            this.humidity = hum;
        
        }
        
        public int getTemp(){
            return temperature;
        }
        
        public int getHum(){
            return humidity;
        }
        
        public void setTemp(int temp){
            temperature = temp;
        }
        
        public void setHum(int hum){
            humidity = hum;
        }
 
    }
    
    
    public int randomNumber(int min, int max) {
        return (int)(Math.random() * (max - min)) + min;
    }

    
    public RoomsSettings(){
        
        
        
        //Map with all the special rooms
        Rooms = new HashMap<String, RoomValues>();
        
        //Neonatal intensive care unit
        Rooms.put("Neonatal Unit", new RoomValues(randomNumber(17,25),randomNumber(39,61))); //the first element is the temperature, the second one is the humidity
        
        //Microbiology
        Rooms.put("Microbiology Unit", new RoomValues(randomNumber(17,23),randomNumber(39,61)));
        
        //Hematology
        Rooms.put("Hematology Unit", new RoomValues(randomNumber(17,23),randomNumber(39,61)));
        
        //Molecular Diagnostics
        Rooms.put("Molecular Diagnostics Unit", new RoomValues(randomNumber(17,25),randomNumber(39,61)));
        
        //Sterile Storage
        Rooms.put("Sterile Storage Unit", new RoomValues(randomNumber(17,21),randomNumber(39,61)));
        
        //Operating Theater
        Rooms.put("Operating Theater Unit", new RoomValues(randomNumber(17,23),randomNumber(39,61)));
        
        //Patient Rooms
        Rooms.put("Room 1", new RoomValues(randomNumber(17,24),randomNumber(29,61)));
        
        //Patient Rooms
        Rooms.put("Room 2", new RoomValues(randomNumber(17,24),randomNumber(29,61)));
        
        //Patient Rooms
        Rooms.put("Room 3", new RoomValues(randomNumber(17,24),randomNumber(29,61)));
        
        //Pharmaceutical Storage
        Rooms.put("Pharmaceutical Storage Unit", new RoomValues(randomNumber(17,26),randomNumber(29,61)));
       
    }
    
    public int[] getRoomValues(String roomName) {
        
        RoomValues room = (RoomValues) Rooms.get(roomName);
        
        if (room != null) {
            
            return new int[]{room.getTemp(), room.getHum()};
            
        }
        return new int[]{-1, -1}; // or negative values if the room or values don't exist
    }
    
    
    
    public static void main(String [] args){
        
        RoomsSettings settings = new RoomsSettings();
        
        int[] values = settings.getRoomValues("Neonatal Unit");
        
        System.out.println("Neonatal Unit Temperature: " + values[0]);
        System.out.println("Neonatal Unit Humidity: " + values[1]);
        
        
    }

    
    
}

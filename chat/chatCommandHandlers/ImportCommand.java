package chat.chatCommandHandlers;


import java.util.LinkedList;

import Parser.ItemsetParser;
import Parser.ItemsetParserSQL;
import Parser.MacroParser;
import Parser.MacroParserSQL;
import Player.Character;
import GameServer.ServerPackets.ServerMessage;
import Gamemaster.GameMaster;
import Player.PlayerConnection;
import Connections.Connection;
import Database.InstallDAO;
import Database.ItemDAO;
import Database.MacroDAO;
import chat.ChatCommandExecutor;

public class ImportCommand implements ChatCommandExecutor {

	private int needsCommandPower;
	
	public ImportCommand(int needsCommandPower){
		this.needsCommandPower=needsCommandPower;
	}
	
	public void execute(String[] parameters, Connection source) {
		System.out.println("Received import command!");
		
		Character cur = ((PlayerConnection)source).getActiveCharacter();
		
		if(!GameMaster.canUseCommand(cur, needsCommandPower)){
			System.out.println("Not enough command power");
			return;
		}
		
		//----------EXPORT ITEMSETS----------
		if(parameters.length>1 && parameters[0].equals("itemset")){
			
			LinkedList<LinkedList<Object>> lines=ItemsetParser.structurize(ItemsetParser.getItemsetlistFromTxt("Data/Itemset.txt"));
			
			//only insert new elements
			if(parameters[1].equals("insertOnly")){
				if(ItemDAO.getInstance().updateAllItemsets(lines, false))
					new ServerMessage().execute("Inserted new itemsets", source);
				return;
			}
			//insert new elements and update existing items
			if(parameters[1].equals("updateAll")){
				if(ItemDAO.getInstance().updateAllItemsets(lines, true))
					new ServerMessage().execute("Inserted and updated new itemsets", source);
				return;
			}
			//delete every element in the database and insert new elements
			if(parameters[1].equals("fullReset")){
				if(ItemDAO.getInstance().deleteAllItemsets()){
					ItemsetParserSQL.parseItemsetToSQL(InstallDAO.getInstance(),lines);
					new ServerMessage().execute("Full itemset reset and upload", source);
				}
			}
		}
		
		//----------EXPORT MACROS----------
		if(parameters.length>1 && parameters[0].equals("macro")){
					
			LinkedList<LinkedList<String>> lines=MacroParser.getMacrolistFromTxt("Data/Macro.txt");
					
			//only insert new elements
			if(parameters[1].equals("insertOnly")){
				if(MacroDAO.getInstance().updateAllMacros(lines, false))
					new ServerMessage().execute("Inserted new macros", source);
				return;
			}
			//insert new elements and update existing items
			if(parameters[1].equals("updateAll")){
				if(MacroDAO.getInstance().updateAllMacros(lines, true))
					new ServerMessage().execute("Inserted and updated new macros", source);
				return;
			}
			//delete every element in the database and insert new elements
			if(parameters[1].equals("fullReset")){
				if(ItemDAO.getInstance().deleteAllItemsets()){
					MacroParserSQL.parseMacroToSQL(InstallDAO.getInstance(),lines);
					new ServerMessage().execute("Full macro reset and upload", source);
				}
			}
		}
		
	}
	
}

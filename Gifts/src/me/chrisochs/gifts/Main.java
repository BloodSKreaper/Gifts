package me.chrisochs.gifts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	private List<Gift> presents = new ArrayList<Gift>();
	private String prefix = "§b~§a+§c-§b~§a+§c-§b~§a+§c-§b~§a+§c-§b~§a+§c-§b~§a+§c-Gifting-Plugin§c-§a+§b~§c-§a+§b~§c-§a+§b~§c-§a+§b~§c-§a+§b~§c-§a+§b~";

	public void onEnable(){
		System.out.println("Lade das supertolle Geschenk-Plugin von BloodSKreaper");
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				if(presents.size()>0){
					List<Gift> toRemove = new ArrayList<>();
					for(Gift g:presents){
						if(g.isExpired()){
							addGiftToPlayerOrDropToGround(g,g.getSender());
							toRemove.add(g);
						}
					}
					presents.removeAll(toRemove);
				}
			}
		}, 20L, 20L);
	}
	public void onDisable(){
		if(presents.size() > 0){
			for(Gift g: presents){
				addGiftToPlayerOrDropToGround(g, g.getSender());
				presents.remove(g);
			}
		}
	}
	
	  public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)
	  {
		  if(!(sender instanceof Player)){
			  sender.sendMessage("Die Gift-Befehle können nur als Spieler ausgeführt werden!");
		  }else{
			  Player p = (Player)sender;
			  sendPrefix(p);
			  //Gift-Command
			  if(cmd.getName().equalsIgnoreCase("gift")){
				  if(args.length < 1 || args.length >1 || getServer().getPlayer(args[0])==null || p.getInventory().getItemInMainHand().getType() == Material.AIR || playerIsSendingPresent(p)){
					  p.sendMessage("Command usage: /gift <PLAYERNAME>");
					  if(args.length < 1){
						  p.sendMessage("§cDu hast keinen Empfänger angegeben!");
					  }else if(args.length > 1){
						  p.sendMessage("§cDu hast zu viele Angaben gemacht!");
					  }else if(getServer().getPlayer(args[0])==null){
						  p.sendMessage("§cDer Spieler "+args[0]+" ist nicht online!");
					  }else if(p.getInventory().getItemInMainHand().getType() == Material.AIR){
						  p.sendMessage("§cDu hast kein Item zum Verschenken in der Hand!");
					  }else{
						  p.sendMessage("§cMomentan führst du bereits einen Schenkvorgang durch. Bitte warte, bis dieser abgeschlossen ist!");
					  }
				  }
				  //Wenn alle Anforderungen erfüllt sind.
				  else{
					  ItemStack present = p.getInventory().getItemInMainHand().clone();
					  presents.add(new Gift(present, p, getServer().getPlayer(args[0])));
					  p.getInventory().getItemInMainHand().setAmount(0);
					  p.sendMessage("§aDu hast ein Geschenk an §6"+getServer().getPlayer(args[0]).getName()+" §agesendet. Allerdings muss es erst noch angenommen werden.");
					  sendPrefix(getServer().getPlayer(args[0]));
					  getServer().getPlayer(args[0]).sendMessage("§6"+p.getName() +" §ahat dir ein Geschenk gesendet! Nimm es mit \"§b/acceptgift "+p.getName()+"§a\" an, oder lehne es mit \"§b/denygift "+p.getName()+"§a\" ab.");
					  getServer().getPlayer(args[0]).sendMessage("§2Du hast hierfür 15 Sekunden Zeit! §6Geschenk: §b"+present.getAmount()+"x"+present.getType().toString());
				  }
			  }
		  
		  
		  //Acceptgift-Command
		  if(cmd.getName().equalsIgnoreCase("acceptgift")){
			  if(args.length < 1 || args.length >1 || getServer().getPlayer(args[0])==null){
				  p.sendMessage("§aBitte gib an, wessen Geschenk du annehmen willst!");
				  p.sendMessage("§aFolgende Geschenke stehen aus:");
				  p.sendMessage("§6"+getPendingGifts(p));
			  }else{
				  Gift pres = null;
				  for(Gift g: presents){
					  if(g.getSender() == getServer().getPlayer(args[0]) && g.getReceiver() == p){
						  pres = g;
					  }
				  }
				  if(pres != null){
					  addGiftToPlayerOrDropToGround(pres, p);
					  presents.remove(pres);
				  }else{
					  p.sendMessage("§cEs ist kein Geschenk von §6"+getServer().getPlayer(args[0]).getName()+" §can dich vorhanden!");
				  }
			  }
		  }
		  //Deny-Command
		  if(cmd.getName().equalsIgnoreCase("denygift")){
			  if(args.length < 1 || args.length >1 || getServer().getPlayer(args[0])==null){
				  p.sendMessage("§aBitte gib an, wessen Geschenk du ablehnen willst!");
				  p.sendMessage("§aFolgende Geschenke stehen aus:");
				  p.sendMessage("§6"+getPendingGifts(p));
			  }else{
				  Gift pres = null;
				  for(Gift g: presents){
					  if(g.getSender() == getServer().getPlayer(args[0]) && g.getReceiver() == p){
						  pres = g;
					  }
				  }
				  if(pres != null){
					  p.sendMessage("§aDu hast das Geschenk von §6"+getServer().getPlayer(args[0]).getName()+" §a§nabgelehnt§2!");
					  addGiftToPlayerOrDropToGround(pres, getServer().getPlayer(args[0]));
					  presents.remove(pres);
				  }else{
					  p.sendMessage("§cEs ist kein Geschenk von §6"+getServer().getPlayer(args[0]).getName()+" §can dich vorhanden!");
				  }
			  }
		  }
		  }
		  return true;
	  }
	
	public boolean playerIsSendingPresent(Player p){
		boolean output = false;
		for(Gift g: presents){
			if(g.getSender() == p){
				output = true;
			}
		}
		return output;
	}
	
	public String getPendingGifts(Player p){
		StringBuilder output = new StringBuilder();
		if(presents.size()>0){
		for(int i = 0; i<presents.size();i++){
			Gift g = presents.get(i);
			if(g.getReceiver() == p){
				if(output.length()>0)output.append("; ");
				output.append(g.getSender().getName() +"-"+g.getPresent().getAmount()+"x "+g.getPresent().getType().name());
				
			}
		}
		}	
		return output.toString();
	}
	
	
	public void addGiftToPlayerOrDropToGround(Gift g, Player p){
		HashMap<Integer, ItemStack> stacks = p.getInventory().addItem(g.getPresent());
		if(p == g.getSender()){
			sendPrefix(p);
			p.sendMessage("§2Du hast dein Geschenk §nzurückerhalten!§6 "+g.getReceiver().getName()+" §2wollte es nicht oder ist afk!");
		}else{
			p.sendMessage("§2Du hast das Geschenk von §6"+g.getSender().getName()+" §2erhalten!");
		}
		if(stacks.size()>0){
			p.sendMessage("§cDa in deinem Inventar nicht genug Platz ist, wurde das Geschenk oder Teile davon gedroppt!");
			for(ItemStack is:stacks.values()){
			p.getLocation().getWorld().dropItem(p.getLocation(), is);
			}
		}
	}
	
	public void sendPrefix(Player p){
		p.sendMessage("");
		p.sendMessage(prefix);
	}
	
	
	
	
	
}



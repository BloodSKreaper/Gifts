package me.chrisochs.gifts;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gift {
	private ItemStack present;
	private Player sender, receiver;
	private long created;//inTimeMillis
	
	public Gift(ItemStack p, Player s, Player r){
		present = p;
		sender = s;
		receiver = r;
		created = System.currentTimeMillis();
	}
	
	public Player getSender(){
		return sender;
	}
	public Player getReceiver(){
		return receiver;
	}
	public ItemStack getPresent(){
		return present;
	}
	public long getCreated(){
		return created;
	}
	public boolean isExpired(){
		long diff = (System.currentTimeMillis()-created)/1000;
		if(diff>15){
			return true;
		}else{
			return false;
		}
	}

}

package org.adorsys.adpharma.platform.rest.exchanges;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.platform.rest.exchanges.exceptions.NoCommandFoundException;
import org.adorsys.adpharma.platform.rest.exchanges.exceptions.NoDrugstoreFoundException;
import org.adorsys.adpharma.platform.rest.exchanges.exceptions.NoProviderFoundException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


@Service
public class AdpHarmaExchangeParser {

	/**
	 * usefull method for transform exchange data to new commande
	 * @author adorsys-clovis
	 *
	 */
	public CommandeFournisseur parseToNewCommande(ExchangeData data,Site drugStore,Fournisseur provider) {
		if( data == null) throw new IllegalArgumentException("data is required");
		CommandeFournisseur command = new CommandeFournisseur();
		command.setSite(drugStore);
		command.setFournisseur(provider);
		command.setCmdNumber(data.getOrderKey());
		command.setCommercialKey(data.getCommercialKey());
		command.setExchangeBeanState(data.getExchangeBeanState());
		command.setSubmitionDate(data.getSubmitionDate());
		command.setTraitmentDate(data.getTraitmentDate());
		//command.setLigneCommande(parseToCommandItems(data.getCommandItems(),command));
		return command ;
	}

	/**
	 * usefull method for transform exchange data to existing  commande
	 * @author adorsys-clovis
	 * 
	 * @param data
	 *        the exchange data to parse
	 * @param command
	 *        existing command
	 * @return the commande
	 * 
	 *
	 */
	public CommandeFournisseur parseToExistingCommande(ExchangeData data,CommandeFournisseur existingCommand){
		if( data == null) throw new IllegalArgumentException("data is required");
		existingCommand.setCmdNumber(data.getOrderKey());
		existingCommand.setCommercialKey(data.getCommercialKey());
		existingCommand.setExchangeBeanState(data.getExchangeBeanState());
		existingCommand.setSubmitionDate(data.getSubmitionDate());
		existingCommand.setTraitmentDate(data.getTraitmentDate());
		List<OrderItems> commandItems = data.getCommandItems();
		Set<LigneCmdFournisseur> ligneCommande = existingCommand.getLigneCommande();
		for (LigneCmdFournisseur line : ligneCommande) {
			for (OrderItems item : commandItems) {
				if(isSameOrderItem(item, line)){
					line.updateLine(item);
					break ;
				}

			}

		}
		return existingCommand ;
	}

	/**
	 *  use for transform Command  to {@link ExchangeData} 
	 * @author adorsys-clovis
	 * 
	 * @param order
	 *        the command to parse
	 * @return exchangeData
	 * 
	 *
	 */
	public ExchangeData parseToTransferableFormat( CommandeFournisseur order){
		ExchangeData data = new ExchangeData();
		if( data == null) throw new IllegalArgumentException("data is required");
		data.setOrderKey(order.getCmdNumber());
		data.setCommercialKey(order.getCommercialKey()); 
		data.setDrugStoreKey(order.getSite().getDrugstoreKey());
		data.setProviderKey(order.getFournisseur().getProviderKey());
		data.setExchangeBeanState(ExchangeBeanState.SUBMIT); //data.setExchangeBeanState(order.getExchangeBeanState());
		data.setSubmitionDate(order.getSubmitionDate());
		data.setTraitmentDate(order.getTraitmentDate());
		data.setCommandItems(parseToOrderItems(order.getLigneCommande()));
		return data ;
	}


	public OrderItems parseToOrderItems(LigneCmdFournisseur item){
		if( item == null) throw new IllegalArgumentException("item is required");
		return new OrderItems(item);
	}

	public List<OrderItems> parseToOrderItems(Set<LigneCmdFournisseur> items){
		if( items == null) throw new IllegalArgumentException("items is required");
		ArrayList<OrderItems> orderItems = new ArrayList<OrderItems>();
		if(!items.isEmpty()){
			for (LigneCmdFournisseur item : items) {
				orderItems.add(new OrderItems(item));
			}
		}
		return orderItems ;
	}

	/*public List<LigneCmdFournisseur> parseToCommandItems(List<OrderItems> items, CommandeFournisseur command){
		if( items == null) throw new IllegalArgumentException("items is required");
		ArrayList<LigneCmdFournisseur> commandItems = new ArrayList<CommandItem>();
		if(!items.isEmpty()){
			for (OrderItems item : items) {
				commandItems.add(new CommandItem(item , command));
			}
		}
		return commandItems ;
	}*/

	public boolean validate(ExchangeData data) throws NoProviderFoundException, NoDrugstoreFoundException, NoCommandFoundException {
		if(data == null ) throw new IllegalArgumentException("exchange data is required");
		String providerKey = StringUtils.isBlank(data.getProviderKey())?"":data.getProviderKey() ;
		String drugstoreKey = StringUtils.isBlank(data.getDrugStoreKey())?"":data.getDrugStoreKey() ;
		String orderKey = StringUtils.isBlank(data.getOrderKey())?"":data.getOrderKey() ;
		if(Fournisseur.findFournisseurByProviderKey(providerKey)==null) throw new NoProviderFoundException("no Provider registered on platform whith providerKey = "+providerKey);
		if(Site.findSiteByDrugstoreKey(drugstoreKey)==null) throw new NoDrugstoreFoundException("no Drugstore registered on platform whith drugstoreKey = "+drugstoreKey);
		if(!ExchangeBeanState.SUBMIT.equals(data.getExchangeBeanState())){
			if(CommandeFournisseur.findCommandByCommandKeyAndDrugstoreKeyAndProviderKey(orderKey, drugstoreKey, providerKey).getResultList().isEmpty()) throw new NoCommandFoundException("no Command found on platform whith commandkey = "+orderKey);
		}
		return true ;
	}

	public boolean isSameOrderItem(OrderItems orderItems ,LigneCmdFournisseur line){
		if(orderItems == null || line==null) return false ;
		if(orderItems.getItemIndex()== line.getIndexLine() ) return true ;
		return false ;
	}
	

}

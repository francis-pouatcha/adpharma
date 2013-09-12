package org.adorsys.adpharma.domain;


public enum Genre {

	  Masculin("Mr.","M"), Feminin("Mme.","F"),Mademoiselle("Mlle.","Mlle"),Enfant("Enft.","Enft"),Docteur("Dr.","Dr"),Neutre(" ","N");
	    
	    private final String salutation ;
	    private final String shortened;

		private Genre(String salutation, String shortened) {
			this.salutation = salutation;
			this.shortened = shortened;
		}

		public String getSalutation() {
			return salutation;
		}

		public String getShortened() {
			return shortened;
		}
		
		public static Genre getGenreByName(String genre){
			if (genre.equals(Genre.Feminin.name())) return Genre.Feminin ;
			if (genre.equals(Genre.Masculin.name())) return Genre.Masculin ;
			if (genre.equals(Genre.Mademoiselle.name())) return Genre.Mademoiselle ;
			if (genre.equals(Genre.Enfant.name())) return Genre.Enfant ;
			if (genre.equals(Genre.Docteur.name())) return Genre.Docteur ;
			if (genre.equals(Genre.Neutre.name())) return Genre.Neutre ;
			return Genre.Neutre ;

			
		}
}

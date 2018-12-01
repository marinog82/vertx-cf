package io.openshift.booster;

import java.util.Arrays;
import java.util.Scanner;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

class CFGenerator {
  // Variabili di istanza
  // -------------------------------------------------------------------------------------------------------------------------------------------------------------
  private String nome, cognome, comune, m, sesso;
  private int anno, giorno;
  
  // Array statici
  private final char[] elencoPari = {'0','1','2','3','4','5','6','7','8','9','A','B',
                                    'C','D','E','F','G','H','I','J','K','L','M','N',
                                    'O','P','Q','R','S','T','U','V','W','X','Y','Z'
                                };
                                   
  private final int[] elencoDispari= {1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 1, 0, 5, 7, 9, 13,
                                15, 17, 19, 21, 2, 4, 18, 20, 11, 3, 6, 8, 12, 14, 16,
                                10, 22, 25, 24, 23
                               };
  
  private final String[][] mese = { {"Gennaio","A"},
                                    {"Febbraio","B"},
                                    {"Marzo","C"},
                                    {"Aprile","D"},
                                    {"Maggio","E"},
                                    {"Giugno","H"},
                                    {"Luglio","L"},
                                    {"Agosto","M"},
                                    {"Settembre","P"},
                                    {"Ottobre","R"},
                                    {"Novembre","S"},
                                    {"Dicembre","T"}
                                  };
  // --------------------------------------------------------------------------------------------------------------------------------------------------------------
  
  
  // Inizializza le variabili di istanza della classe
  // --------------------------------------------------------------------------------------------------------------------------------------------------------------
  CFGenerator(String nome, String cognome, String comune, String m, int anno, int giorno,String sesso) {
    this.nome = nome;
    this.cognome = cognome;
    this.comune = comune;
    this.m = m;
    this.anno = anno;
    this.giorno = giorno;
    this.sesso = sesso;
   
  } // Fine costruttore
  // -----------------------------------------------------------------------------------------------------------------------------------------------------------------
  
  
  // Metogi getter per ottenere gli elementi della classe
  // Interfacce più comode ed ordinate per l'accesso alle funzionalità
  // -----------------------------------------------------------------------------------------------------------------------------------------------------------------
  String getNome() {
    return modificaNC(nome,true);
  }
  String getCognome() {
    return modificaNC(cognome,false);
  }
  String getNomeInserito() {
    return nome;
  }
  String getCognomeInserito() {
    return cognome;
  }
  String getMese() {
    return modificaMese();
  }
  String getMeseInserito() {
    return m;
  }
  int getAnno() {
    return (anno%100);
  }
  int getAnnoInserito() {
    return anno;
  }
  int getGiorno() {
    return (sesso.equals("M")) ? giorno : (giorno+40);
  }
  int getGiornoInserito() {
    return giorno;
  }
  String getComune() {
    return elaboraCodiceComune();
  }
  String getCodice() {
    return calcolaCodice();
  }
  String getCodiceFiscale() {
    return toString();
  }
  // -----------------------------------------------------------------------------------------------------------------------------------------------------------
  
  
  // I seguenti metodi svolgono le operazioni specifiche sui dati
  
  /**
       * @param stringa                  Corrisponde al nome/cognome da modificare
       * @param cod                       Se cod e' true, indica il nome; altrimenti il cognome
       * @return nuovaStringa       Restituisce la stringa modificata
       */
  // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private String modificaNC(String stringa, boolean cod) {
    String nuovastringa = "";
    stringa = stringa.replaceAll(" ", "");           // Rimuovo eventuali spazi
    stringa = stringa.toLowerCase();
    
    String consonanti = getConsonanti(stringa);      // Ottengo tutte le consonanti e tutte le vocali della stringa
    String vocali = getVocali(stringa);
    
    // Controlla i possibili casi
    if(consonanti.length() == 3) {                   // La stringa contiene solo 3 consonanti, quindi ho gia' la modifica
      nuovastringa = consonanti;
    }
                                                     // Le consonanti non sono sufficienti, e la stinga e' più lunga o
                                                     // uguale a 3 caratteri [aggiungo le vocali mancanti]
    else if((consonanti.length() < 3) && (stringa.length() >= 3)) {
      nuovastringa = consonanti;
      nuovastringa = aggiungiVocali(nuovastringa, vocali);
    } 
                                                     // Le consonanti non sono sufficienti, e la stringa 
                                                     //contiene meno di 3 caratteri [aggiungo consonanti e vocali, e le x]
    else if((consonanti.length() < 3) && (stringa.length() < 3)) {
      nuovastringa = consonanti;
      nuovastringa += vocali;
      nuovastringa = aggiungiX(nuovastringa);
    } 
                                                     // Le consonanti sono in eccesso, prendo solo le 
                                                     //prime 3 nel caso del cognome; nel caso del nome la 0, 2, 3
    else if(consonanti.length() > 3) {
      // true indica il nome e false il cognome
      if(!cod) nuovastringa = consonanti.substring(0,3);
      else nuovastringa = consonanti.charAt(0) +""+ consonanti.charAt(2) +""+ consonanti.charAt(3);
    }
    
    return nuovastringa;
  }
  // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  
  
  // Aggiunge le X sino a raggiungere una lunghezza complessiva di 3 caratteri
  // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private String aggiungiX(String stringa) {
    while(stringa.length() < 3) {
      stringa += "x";
    }
    return stringa;
  }
  // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  
  // Aggiunge le vocali alla stringa passata per parametro
  // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private String aggiungiVocali(String stringa, String vocali) {
    int index = 0;
    while(stringa.length() < 3) {
      stringa += vocali.charAt(index);
      index++;
    }
    return stringa; 
  }
  // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  
  // Toglie dalla stringa tutte le consonanti
  // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private String getVocali(String stringa) {
    stringa = stringa.replaceAll("[^aeiou]", "");
    return stringa;
  }
  // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  
  // Toglie dalla stringa tutte le vocali
  // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private String getConsonanti(String stringa) {
    stringa = stringa.replaceAll("[aeiou]","");
    return stringa;
  }
  // --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  
  // Restituisce il codice del mese
  // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private String modificaMese() {
    for(int i=0; i<mese.length; i++) {
      if(mese[i][0].equalsIgnoreCase(m)) return mese[i][1];
    }
    return null;
  }
  // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  
  // Elabora codice del comune
  // ------ --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private String elaboraCodiceComune() {
  String cc="";
    try {
      InputStream in = getClass().getResourceAsStream("/comuni.txt");
      Scanner scanner = new Scanner(in);//file);
      scanner.useDelimiter("\r\n");
      
      while(scanner.hasNext()) {
        String s1 = scanner.nextLine();
        String s2 = s1.substring(0,s1.indexOf('-')-1);
        // System.out.println(s2);
        if(s2.equalsIgnoreCase(comune)) {
          cc = s1.substring(s1.lastIndexOf(' ')+1);
        }
      }
      
      scanner.close();
    } catch(Exception e) {e.printStackTrace();}
    return cc;
  }
  // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 
  // Calcolo del Codice di Controllo
  // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private String calcolaCodice() {
    String str = getCognome().toUpperCase()+getNome().toUpperCase()+getAnno()+getMese()+getGiorno()+getComune();
    int pari=0,dispari=0;
    
    for(int i=0; i<str.length(); i++) {
      char ch = str.charAt(i);              // i-esimo carattere della stringa
      
      // Il primo carattere e' il numero 1 non 0
      if((i+1) % 2 == 0) {
        int index = Arrays.binarySearch(elencoPari,ch);
        pari += (index >= 10) ? index-10 : index;
      } else {
        int index = Arrays.binarySearch(elencoPari,ch);
        dispari += elencoDispari[index];
      }
    }
    
    int controllo = (pari+dispari) % 26;
    controllo += 10;
    
    return elencoPari[controllo]+"";
  }
  // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  
  // Viene richiamato per una stampa
  public String toString() {
    return getCognome().toUpperCase()+getNome().toUpperCase()+getAnno()+getMese()+getGiorno()+getComune()+getCodice();
  }
  
}
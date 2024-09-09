/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

import tiendacocina.MenuPrincipal;

/**
 *
 * @author SENA 10
 */
public class MenuPrincipalSingleton {
    
    private MenuPrincipalSingleton() {}
    
    private static MenuPrincipal menuPrincipal = null;
    
    public static MenuPrincipal getInstance() {
        if (menuPrincipal != null){
            return menuPrincipal;
        }
        
        menuPrincipal = new MenuPrincipal();
        return menuPrincipal;
    }
    
}

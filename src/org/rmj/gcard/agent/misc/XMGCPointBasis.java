/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rmj.gcard.agent.misc;

import java.util.Date;
import javax.swing.JDialog;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.iface.XMRecord;
import org.rmj.gcard.base.misc.GCPointBasis;
import org.rmj.integsys.pojo.UnitGCPointsBasis;

/**
 *
 * @author kalyptus
 */
public class XMGCPointBasis implements XMRecord{

   public XMGCPointBasis(GRider foGRider, String fsBranchCd, boolean fbWithParent){
      this.poGRider = foGRider;
      if(foGRider != null){
         this.psBranchCd = fsBranchCd;
         poCtrl = new GCPointBasis();
         poCtrl.setGRider(foGRider);
         poCtrl.setBranch(psBranchCd);
         poCtrl.setWithParent(fbWithParent);
         pnEditMode = EditMode.UNKNOWN;
      }
   }

   public void setMaster(String fsCol, Object foData){
      setMaster(poData.getColumn(fsCol), foData);
   }

    public void setMaster(int fnCol, Object foData) {
      if(pnEditMode != EditMode.UNKNOWN && poCtrl != null){
         if(!(fnCol == poData.getColumn("sSourceCd") ||
             fnCol == poData.getColumn("cRecdStat") ||
             fnCol == poData.getColumn("sModified") ||
             fnCol == poData.getColumn("dModified"))){
             if(fnCol == poData.getColumn("dTransact")){
                if(foData instanceof Date)
                   poData.setValue(fnCol, foData);
                else
                   poData.setValue(fnCol, null);
             }
             else if(fnCol == poData.getColumn("nAmtPerPt") ||
                     fnCol == poData.getColumn("nMinPoint") ||
                     fnCol == poData.getColumn("nMaxPoint") ||
                     fnCol == poData.getColumn("nDiscount") ||
                     fnCol == poData.getColumn("nBonusPnt")){
                if(foData instanceof Number)
                   poData.setValue(fnCol, foData);
                else
                   poData.setValue(fnCol, 0.00);
             }
             else{
                poData.setValue(fnCol, foData);
             }
          }
       }
    }

    public Object getMaster(String fsCol){
      return getMaster(poData.getColumn(fsCol));
    }

    public Object getMaster(int fnCol) {
      if(pnEditMode == EditMode.UNKNOWN || poCtrl == null)
         return null;
      else{
         return poData.getValue(fnCol);
      }
    }

    public boolean newRecord() {
      if(poCtrl == null){
         return false;
      }

      poData = poCtrl.newRecord();

      if(poData == null){
         return false;
      }
      else{

         //set the values of foreign key(object) to null
         poCmpny = null;

         pnEditMode = EditMode.ADDNEW;
         return true;
      }
    }

    public boolean openRecord(String fstransNox) {
      if(poCtrl == null){
         return false;
      }

      poData = poCtrl.openRecord(fstransNox);

      if(poData.getSourceCd() == null){
         return false;
      }
      else{
         //set the values of foreign key(object) to null
         poCmpny = null;

         pnEditMode = EditMode.READY;
         return true;
      }
    }

    public boolean updateRecord() {
      if(poCtrl == null){
         return false;
      }
      else if(pnEditMode != EditMode.READY) {
         return false;
      }
      else{
         pnEditMode = EditMode.UPDATE;
         return true;
      }
    }

    public boolean saveRecord() {
      if(poCtrl == null){
         return false;
      }
      else if(pnEditMode == EditMode.UNKNOWN){
         return false;
      }
      else{
         UnitGCPointsBasis loResult;
         if(pnEditMode == EditMode.ADDNEW)
            loResult = poCtrl.saveRecord(poData, "");
         else
            loResult = poCtrl.saveRecord(poData, (String) poData.getValue(1));

         if(loResult == null)
            return false;
         else{
            pnEditMode = EditMode.READY;
            poData = loResult;
            return true;
         }
      }
    }

    public boolean deleteRecord(String fsTransNox) {
      if(poCtrl == null){
         return false;
      }
      else if(pnEditMode != EditMode.READY){
         return false;
      }
      else{
         boolean lbResult = poCtrl.deleteRecord(fsTransNox);
         if(lbResult)
            pnEditMode = EditMode.UNKNOWN;

         return lbResult;
      }
    }

    public boolean deactivateRecord(String fsTransNox) {
      if(poCtrl == null){
         return false;
      }
      else if(pnEditMode != EditMode.READY){
         return false;
      }
      else{
         boolean lbResult = poCtrl.deactivateRecord(fsTransNox);
         if(lbResult)
            pnEditMode = EditMode.UNKNOWN;

         return lbResult;
      }
    }

    public boolean activateRecord(String fsTransNox) {
      if(poCtrl == null){
         return false;
      }
      else if(pnEditMode != EditMode.READY){
         return false;
      }
      else{
         boolean lbResult = poCtrl.activateRecord(fsTransNox);
         if(lbResult)
            pnEditMode = EditMode.UNKNOWN;

         return lbResult;
      }
    }

    public void setBranch(String fsBranchCD) {
      psBranchCd = fsBranchCD;
      poCtrl.setBranch(fsBranchCD);
    }

    public int getEditMode() {
      return pnEditMode;
    }

   // Added methods here
   public void setGRider(GRider foGRider) {
      this.poGRider = foGRider;
      poCtrl.setGRider(foGRider);
   }

   public XMGCPartner getCompany(){
      if(poCmpny == null)
         poCmpny = new XMGCPartner(poGRider, psBranchCd, true);

      poCmpny.openRecord(poData.getCompnyID());
      return poCmpny;
   }

   private boolean pbWithParnt;
   private UnitGCPointsBasis poData;
   private GCPointBasis poCtrl;
   private GRider poGRider;
   private int pnEditMode;
   private String psBranchCd;

   private XMGCPartner poCmpny = null;

}

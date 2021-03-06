/*******************************************************************************
**NOTE** This code was generated by a tool and will occasionally be
overwritten. We welcome comments and issues regarding this code; they will be
addressed in the generation tool. If you wish to submit pull requests, please
do so for the templates in that tool.

This code was generated by Vipr (https://github.com/microsoft/vipr) using
the T4TemplateWriter (https://github.com/msopentech/vipr-t4templatewriter).

Copyright (c) Microsoft Corporation. All Rights Reserved.
Licensed under the Apache License 2.0; see LICENSE in the source repository
root for authoritative license information.﻿
******************************************************************************/
package com.microsoft.services.outlook;





/**
 * The type Contact.
*/
public class Contact extends Item {

    public Contact(){
        setODataType("#Microsoft.OutlookServices.Contact");
    }
            
    private String ParentFolderId;
     
    /**
    * Gets the Parent Folder Id.
    *
    * @return the String
    */
    public String getParentFolderId() {
        return this.ParentFolderId; 
    }

    /**
    * Sets the Parent Folder Id.
    *
    * @param value the String
    */
    public void setParentFolderId(String value) { 
        this.ParentFolderId = value; 
        valueChanged("ParentFolderId", value);

    }
            
    private java.util.Calendar Birthday;
     
    /**
    * Gets the Birthday.
    *
    * @return the java.util.Calendar
    */
    public java.util.Calendar getBirthday() {
        return this.Birthday; 
    }

    /**
    * Sets the Birthday.
    *
    * @param value the java.util.Calendar
    */
    public void setBirthday(java.util.Calendar value) { 
        this.Birthday = value; 
        valueChanged("Birthday", value);

    }
            
    private String FileAs;
     
    /**
    * Gets the File As.
    *
    * @return the String
    */
    public String getFileAs() {
        return this.FileAs; 
    }

    /**
    * Sets the File As.
    *
    * @param value the String
    */
    public void setFileAs(String value) { 
        this.FileAs = value; 
        valueChanged("FileAs", value);

    }
            
    private String DisplayName;
     
    /**
    * Gets the Display Name.
    *
    * @return the String
    */
    public String getDisplayName() {
        return this.DisplayName; 
    }

    /**
    * Sets the Display Name.
    *
    * @param value the String
    */
    public void setDisplayName(String value) { 
        this.DisplayName = value; 
        valueChanged("DisplayName", value);

    }
            
    private String GivenName;
     
    /**
    * Gets the Given Name.
    *
    * @return the String
    */
    public String getGivenName() {
        return this.GivenName; 
    }

    /**
    * Sets the Given Name.
    *
    * @param value the String
    */
    public void setGivenName(String value) { 
        this.GivenName = value; 
        valueChanged("GivenName", value);

    }
            
    private String Initials;
     
    /**
    * Gets the Initials.
    *
    * @return the String
    */
    public String getInitials() {
        return this.Initials; 
    }

    /**
    * Sets the Initials.
    *
    * @param value the String
    */
    public void setInitials(String value) { 
        this.Initials = value; 
        valueChanged("Initials", value);

    }
            
    private String MiddleName;
     
    /**
    * Gets the Middle Name.
    *
    * @return the String
    */
    public String getMiddleName() {
        return this.MiddleName; 
    }

    /**
    * Sets the Middle Name.
    *
    * @param value the String
    */
    public void setMiddleName(String value) { 
        this.MiddleName = value; 
        valueChanged("MiddleName", value);

    }
            
    private String NickName;
     
    /**
    * Gets the Nick Name.
    *
    * @return the String
    */
    public String getNickName() {
        return this.NickName; 
    }

    /**
    * Sets the Nick Name.
    *
    * @param value the String
    */
    public void setNickName(String value) { 
        this.NickName = value; 
        valueChanged("NickName", value);

    }
            
    private String Surname;
     
    /**
    * Gets the Surname.
    *
    * @return the String
    */
    public String getSurname() {
        return this.Surname; 
    }

    /**
    * Sets the Surname.
    *
    * @param value the String
    */
    public void setSurname(String value) { 
        this.Surname = value; 
        valueChanged("Surname", value);

    }
            
    private String Title;
     
    /**
    * Gets the Title.
    *
    * @return the String
    */
    public String getTitle() {
        return this.Title; 
    }

    /**
    * Sets the Title.
    *
    * @param value the String
    */
    public void setTitle(String value) { 
        this.Title = value; 
        valueChanged("Title", value);

    }
            
    private String YomiGivenName;
     
    /**
    * Gets the Yomi Given Name.
    *
    * @return the String
    */
    public String getYomiGivenName() {
        return this.YomiGivenName; 
    }

    /**
    * Sets the Yomi Given Name.
    *
    * @param value the String
    */
    public void setYomiGivenName(String value) { 
        this.YomiGivenName = value; 
        valueChanged("YomiGivenName", value);

    }
            
    private String YomiSurname;
     
    /**
    * Gets the Yomi Surname.
    *
    * @return the String
    */
    public String getYomiSurname() {
        return this.YomiSurname; 
    }

    /**
    * Sets the Yomi Surname.
    *
    * @param value the String
    */
    public void setYomiSurname(String value) { 
        this.YomiSurname = value; 
        valueChanged("YomiSurname", value);

    }
            
    private String YomiCompanyName;
     
    /**
    * Gets the Yomi Company Name.
    *
    * @return the String
    */
    public String getYomiCompanyName() {
        return this.YomiCompanyName; 
    }

    /**
    * Sets the Yomi Company Name.
    *
    * @param value the String
    */
    public void setYomiCompanyName(String value) { 
        this.YomiCompanyName = value; 
        valueChanged("YomiCompanyName", value);

    }
            
    private String Generation;
     
    /**
    * Gets the Generation.
    *
    * @return the String
    */
    public String getGeneration() {
        return this.Generation; 
    }

    /**
    * Sets the Generation.
    *
    * @param value the String
    */
    public void setGeneration(String value) { 
        this.Generation = value; 
        valueChanged("Generation", value);

    }
    
        
    private java.util.List<EmailAddress> EmailAddresses = null;
    
    
     
    /**
    * Gets the Email Addresses.
    *
    * @return the java.util.List<EmailAddress>
    */
    public java.util.List<EmailAddress> getEmailAddresses() {
        return this.EmailAddresses; 
    }

    /**
    * Sets the Email Addresses.
    *
    * @param value the java.util.List<EmailAddress>
    */
    public void setEmailAddresses(java.util.List<EmailAddress> value) { 
        this.EmailAddresses = value; 
        valueChanged("EmailAddresses", value);

    }
    
        
    private java.util.List<String> ImAddresses = null;
    
    
     
    /**
    * Gets the Im Addresses.
    *
    * @return the java.util.List<String>
    */
    public java.util.List<String> getImAddresses() {
        return this.ImAddresses; 
    }

    /**
    * Sets the Im Addresses.
    *
    * @param value the java.util.List<String>
    */
    public void setImAddresses(java.util.List<String> value) { 
        this.ImAddresses = value; 
        valueChanged("ImAddresses", value);

    }
            
    private String JobTitle;
     
    /**
    * Gets the Job Title.
    *
    * @return the String
    */
    public String getJobTitle() {
        return this.JobTitle; 
    }

    /**
    * Sets the Job Title.
    *
    * @param value the String
    */
    public void setJobTitle(String value) { 
        this.JobTitle = value; 
        valueChanged("JobTitle", value);

    }
            
    private String CompanyName;
     
    /**
    * Gets the Company Name.
    *
    * @return the String
    */
    public String getCompanyName() {
        return this.CompanyName; 
    }

    /**
    * Sets the Company Name.
    *
    * @param value the String
    */
    public void setCompanyName(String value) { 
        this.CompanyName = value; 
        valueChanged("CompanyName", value);

    }
            
    private String Department;
     
    /**
    * Gets the Department.
    *
    * @return the String
    */
    public String getDepartment() {
        return this.Department; 
    }

    /**
    * Sets the Department.
    *
    * @param value the String
    */
    public void setDepartment(String value) { 
        this.Department = value; 
        valueChanged("Department", value);

    }
            
    private String OfficeLocation;
     
    /**
    * Gets the Office Location.
    *
    * @return the String
    */
    public String getOfficeLocation() {
        return this.OfficeLocation; 
    }

    /**
    * Sets the Office Location.
    *
    * @param value the String
    */
    public void setOfficeLocation(String value) { 
        this.OfficeLocation = value; 
        valueChanged("OfficeLocation", value);

    }
            
    private String Profession;
     
    /**
    * Gets the Profession.
    *
    * @return the String
    */
    public String getProfession() {
        return this.Profession; 
    }

    /**
    * Sets the Profession.
    *
    * @param value the String
    */
    public void setProfession(String value) { 
        this.Profession = value; 
        valueChanged("Profession", value);

    }
            
    private String BusinessHomePage;
     
    /**
    * Gets the Business Home Page.
    *
    * @return the String
    */
    public String getBusinessHomePage() {
        return this.BusinessHomePage; 
    }

    /**
    * Sets the Business Home Page.
    *
    * @param value the String
    */
    public void setBusinessHomePage(String value) { 
        this.BusinessHomePage = value; 
        valueChanged("BusinessHomePage", value);

    }
            
    private String AssistantName;
     
    /**
    * Gets the Assistant Name.
    *
    * @return the String
    */
    public String getAssistantName() {
        return this.AssistantName; 
    }

    /**
    * Sets the Assistant Name.
    *
    * @param value the String
    */
    public void setAssistantName(String value) { 
        this.AssistantName = value; 
        valueChanged("AssistantName", value);

    }
            
    private String Manager;
     
    /**
    * Gets the Manager.
    *
    * @return the String
    */
    public String getManager() {
        return this.Manager; 
    }

    /**
    * Sets the Manager.
    *
    * @param value the String
    */
    public void setManager(String value) { 
        this.Manager = value; 
        valueChanged("Manager", value);

    }
    
        
    private java.util.List<String> HomePhones = null;
    
    
     
    /**
    * Gets the Home Phones.
    *
    * @return the java.util.List<String>
    */
    public java.util.List<String> getHomePhones() {
        return this.HomePhones; 
    }

    /**
    * Sets the Home Phones.
    *
    * @param value the java.util.List<String>
    */
    public void setHomePhones(java.util.List<String> value) { 
        this.HomePhones = value; 
        valueChanged("HomePhones", value);

    }
            
    private String MobilePhone1;
     
    /**
    * Gets the Mobile Phone1.
    *
    * @return the String
    */
    public String getMobilePhone1() {
        return this.MobilePhone1; 
    }

    /**
    * Sets the Mobile Phone1.
    *
    * @param value the String
    */
    public void setMobilePhone1(String value) { 
        this.MobilePhone1 = value; 
        valueChanged("MobilePhone1", value);

    }
    
        
    private java.util.List<String> BusinessPhones = null;
    
    
     
    /**
    * Gets the Business Phones.
    *
    * @return the java.util.List<String>
    */
    public java.util.List<String> getBusinessPhones() {
        return this.BusinessPhones; 
    }

    /**
    * Sets the Business Phones.
    *
    * @param value the java.util.List<String>
    */
    public void setBusinessPhones(java.util.List<String> value) { 
        this.BusinessPhones = value; 
        valueChanged("BusinessPhones", value);

    }
            
    private PhysicalAddress HomeAddress;
     
    /**
    * Gets the Home Address.
    *
    * @return the PhysicalAddress
    */
    public PhysicalAddress getHomeAddress() {
        return this.HomeAddress; 
    }

    /**
    * Sets the Home Address.
    *
    * @param value the PhysicalAddress
    */
    public void setHomeAddress(PhysicalAddress value) { 
        this.HomeAddress = value; 
        valueChanged("HomeAddress", value);

    }
            
    private PhysicalAddress BusinessAddress;
     
    /**
    * Gets the Business Address.
    *
    * @return the PhysicalAddress
    */
    public PhysicalAddress getBusinessAddress() {
        return this.BusinessAddress; 
    }

    /**
    * Sets the Business Address.
    *
    * @param value the PhysicalAddress
    */
    public void setBusinessAddress(PhysicalAddress value) { 
        this.BusinessAddress = value; 
        valueChanged("BusinessAddress", value);

    }
            
    private PhysicalAddress OtherAddress;
     
    /**
    * Gets the Other Address.
    *
    * @return the PhysicalAddress
    */
    public PhysicalAddress getOtherAddress() {
        return this.OtherAddress; 
    }

    /**
    * Sets the Other Address.
    *
    * @param value the PhysicalAddress
    */
    public void setOtherAddress(PhysicalAddress value) { 
        this.OtherAddress = value; 
        valueChanged("OtherAddress", value);

    }
            
    private String SpouseName;
     
    /**
    * Gets the Spouse Name.
    *
    * @return the String
    */
    public String getSpouseName() {
        return this.SpouseName; 
    }

    /**
    * Sets the Spouse Name.
    *
    * @param value the String
    */
    public void setSpouseName(String value) { 
        this.SpouseName = value; 
        valueChanged("SpouseName", value);

    }
            
    private String PersonalNotes;
     
    /**
    * Gets the Personal Notes.
    *
    * @return the String
    */
    public String getPersonalNotes() {
        return this.PersonalNotes; 
    }

    /**
    * Sets the Personal Notes.
    *
    * @param value the String
    */
    public void setPersonalNotes(String value) { 
        this.PersonalNotes = value; 
        valueChanged("PersonalNotes", value);

    }
    
        
    private java.util.List<String> Children = null;
    
    
     
    /**
    * Gets the Children.
    *
    * @return the java.util.List<String>
    */
    public java.util.List<String> getChildren() {
        return this.Children; 
    }

    /**
    * Sets the Children.
    *
    * @param value the java.util.List<String>
    */
    public void setChildren(java.util.List<String> value) { 
        this.Children = value; 
        valueChanged("Children", value);

    }
            
    private Photo Photo;
     
    /**
    * Gets the Photo.
    *
    * @return the Photo
    */
    public Photo getPhoto() {
        return this.Photo; 
    }

    /**
    * Sets the Photo.
    *
    * @param value the Photo
    */
    public void setPhoto(Photo value) { 
        this.Photo = value; 
        valueChanged("Photo", value);

    }
}


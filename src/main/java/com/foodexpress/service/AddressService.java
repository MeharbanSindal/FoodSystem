package com.foodexpress.service;

import com.foodexpress.dao.AddressRepo;
import com.foodexpress.dao.UserRepo;
import com.foodexpress.model.Address;
import com.foodexpress.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressService {

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private UserRepo userRepo;

    public List<Address> getAddressesForUser(String userEmail) {
        User user = userRepo.findByEmail(userEmail);
        if (user == null) {
            return List.of();
        }
        return addressRepo.findByUserId(user.getId());
    }

    public Address getAddressForUser(Long addressId, String userEmail) {
        if (addressId == null) {
            return null;
        }
        User user = userRepo.findByEmail(userEmail);
        if (user == null) {
            return null;
        }
        Address address = addressRepo.findById(addressId);
        if (address == null || address.getUser() == null || !address.getUser().getId().equals(user.getId())) {
            return null;
        }
        return address;
    }

    public boolean addAddress(String userEmail,
                              String label,
                              String addressLine,
                              String city,
                              String state,
                              String pincode,
                              String landmark,
                              Double latitude,
                              Double longitude,
                              boolean defaultAddress) {
        User user = userRepo.findByEmail(userEmail);
        if (user == null || addressLine == null || addressLine.trim().isEmpty()) {
            return false;
        }

        Address address = new Address();
        address.setUser(user);
        address.setLabel(trimToNull(label));
        address.setAddressLine(addressLine.trim());
        address.setCity(trimToNull(city));
        address.setState(trimToNull(state));
        address.setPincode(trimToNull(pincode));
        address.setLandmark(trimToNull(landmark));
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address.setDefaultAddress(defaultAddress);

        if (defaultAddress) {
            addressRepo.clearDefaultForUser(user.getId());
        }

        addressRepo.saveOrUpdate(address);
        return true;
    }

    public boolean deleteAddress(String userEmail, Long addressId) {
        Address address = getAddressForUser(addressId, userEmail);
        if (address == null) {
            return false;
        }
        addressRepo.delete(address);
        return true;
    }

    public boolean markDefaultAddress(String userEmail, Long addressId) {
        Address address = getAddressForUser(addressId, userEmail);
        if (address == null || address.getUser() == null) {
            return false;
        }
        addressRepo.clearDefaultForUser(address.getUser().getId());
        address.setDefaultAddress(true);
        addressRepo.saveOrUpdate(address);
        return true;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

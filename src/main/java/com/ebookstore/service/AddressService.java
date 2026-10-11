package com.ebookstore.service;

import com.ebookstore.dto.AddressDto;
import com.ebookstore.dto.AddressRequest;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.Address;
import com.ebookstore.model.User;
import com.ebookstore.repository.AddressRepository;
import com.ebookstore.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    /** Returns all saved delivery addresses for the given user. */
    public List<AddressDto> getAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Saves a new delivery address for the user. If {@code isDefault} is true,
     * any previously default address is cleared first.
     */
    public AddressDto addAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (request.isDefault()) {
            // Clear existing default addresses for this user
            List<Address> existing = addressRepository.findByUserId(userId);
            existing.stream()
                    .filter(Address::isDefault)
                    .forEach(a -> a.setDefault(false));
            addressRepository.saveAll(existing);
        }

        Address address = new Address(
                user,
                request.getLine1(),
                request.getLine2(),
                request.getCity(),
                request.getState(),
                request.getPincode(),
                request.isDefault());

        return toDto(addressRepository.save(address));
    }

    /** Deletes an address that belongs to the given user. */
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found: " + addressId));
        addressRepository.delete(address);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private AddressDto toDto(Address a) {
        return new AddressDto(a.getId(), a.getLine1(), a.getLine2(),
                a.getCity(), a.getState(), a.getPincode(), a.isDefault());
    }
}

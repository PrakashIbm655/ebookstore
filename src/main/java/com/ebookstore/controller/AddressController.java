package com.ebookstore.controller;

import com.ebookstore.dto.AddressDto;
import com.ebookstore.dto.AddressRequest;
import com.ebookstore.security.AuthenticatedUser;
import com.ebookstore.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me/addresses")
@Tag(name = "Addresses", description = "User delivery address management endpoints")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /** GET /users/me/addresses — list saved delivery addresses for the current user. */
    @GetMapping
    @Operation(summary = "List saved delivery addresses for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Addresses retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<AddressDto>> getAddresses(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(addressService.getAddresses(principal.userId()));
    }

    /** POST /users/me/addresses — add a new delivery address; returns 201. */
    @PostMapping
    @Operation(summary = "Add a new delivery address")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Address created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid address request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<AddressDto> addAddress(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody AddressRequest request) {
        AddressDto created = addressService.addAddress(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** DELETE /users/me/addresses/{id} — delete an address owned by the current user; returns 204. */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a delivery address by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long id) {
        addressService.deleteAddress(principal.userId(), id);
        return ResponseEntity.noContent().build();
    }
}

// Main JavaScript file for Real Estate Management System

document.addEventListener('DOMContentLoaded', function() {
    // Initialize components
    initializeNavbar();
    initializeModernSearch();
    initializePropertyNavigation();
    initializeForms();
    initializeFavorites();
});

// Navbar functionality
function initializeNavbar() {
    // Mobile menu toggle
    const menuToggle = document.querySelector('.menu-toggle');
    const navLinks = document.querySelector('.nav-links');
    
    if (menuToggle && navLinks) {
        menuToggle.addEventListener('click', function() {
            navLinks.classList.toggle('active');
        });
    }
}

// Modern search functionality
function initializeModernSearch() {
    console.log("Initializing modern search...");
    
    // Advanced filters toggle with smooth animation
    const filterToggle = document.querySelector('.filter-toggle');
    const expandedFilters = document.querySelector('.expanded-filters');
    
    if (filterToggle && expandedFilters) {
        console.log("Filter toggle and expanded filters found");
        
        // Initialize expanded filters to ensure proper starting state
        expandedFilters.style.height = '0px';
        expandedFilters.style.overflow = 'hidden';
        
        // Function to calculate the proper height for expanded filters - simplified for performance
        function getExpandedHeight() {
            // Temporarily make visible but keep out of layout flow
            const originalStyles = {
                position: expandedFilters.style.position,
                visibility: expandedFilters.style.visibility,
                height: expandedFilters.style.height
            };
            
            // Set to auto height but invisible for measurement
            expandedFilters.style.position = 'absolute';
            expandedFilters.style.visibility = 'hidden';
            expandedFilters.style.height = 'auto';
            
            // Get the height
            const height = expandedFilters.offsetHeight;
            
            // Restore original styles
            expandedFilters.style.position = originalStyles.position;
            expandedFilters.style.visibility = originalStyles.visibility;
            expandedFilters.style.height = originalStyles.height;
            
            return height;
        }
        
        filterToggle.addEventListener('click', function() {
            console.log("Toggle clicked");
            
            // Toggle icon
            const icon = this.querySelector('i');
            
            if (!expandedFilters.classList.contains('active')) {
                // Opening the filters
                expandedFilters.classList.add('active');
                const expandedHeight = getExpandedHeight();
                
                // Immediately set styles for fast expansion
                expandedFilters.style.height = expandedHeight + 'px';
                expandedFilters.style.visibility = 'visible';
                
                // After animation completes, ensure it's properly visible
                setTimeout(() => {
                    if (expandedFilters.classList.contains('active')) {
                        expandedFilters.style.overflow = 'visible';
                    }
                }, 210); // slightly longer than the CSS transition
                
                icon.classList.remove('fa-chevron-down');
                icon.classList.add('fa-chevron-up');
            } else {
                // Closing the filters
                expandedFilters.style.overflow = 'hidden';
                expandedFilters.style.height = '0px';
                
                icon.classList.remove('fa-chevron-up');
                icon.classList.add('fa-chevron-down');
                
                // Remove the active class after transition ends
                setTimeout(() => {
                    if (!expandedFilters.classList.contains('active') || expandedFilters.style.height === '0px') {
                        expandedFilters.classList.remove('active');
                        expandedFilters.style.visibility = 'hidden';
                    }
                }, 210); // slightly longer than the CSS transition
            }
        });
        
        // Handle window resize for responsive filters
        window.addEventListener('resize', function() {
            if (expandedFilters.classList.contains('active')) {
                expandedFilters.style.height = 'auto';
                expandedFilters.style.overflow = 'visible';
            }
        });
    } else {
        console.log("Could not find filter toggle or expanded filters");
    }
    
    // Handle search submission
    const searchBtn = document.querySelector('.search-btn');
    const mainSearchInput = document.getElementById('main-search');
    const searchForm = document.getElementById('property-search-form');
    const hiddenSearchInput = document.getElementById('search');
    
    if (searchBtn && mainSearchInput && searchForm && hiddenSearchInput) {
        console.log("Search elements found");
        
        searchBtn.addEventListener('click', function(e) {
            e.preventDefault();
            console.log("Search button clicked");
            // Transfer the main search input value to the hidden input in the form
            hiddenSearchInput.value = mainSearchInput.value;
            console.log("Search value transferred:", mainSearchInput.value);
            // Submit the form
            searchForm.submit();
        });
        
        // Also submit on Enter key in main search input
        mainSearchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                hiddenSearchInput.value = mainSearchInput.value;
                searchForm.submit();
            }
        });
    } else {
        console.log("Could not find search elements:", {
            searchBtn: !!searchBtn,
            mainSearchInput: !!mainSearchInput,
            searchForm: !!searchForm,
            hiddenSearchInput: !!hiddenSearchInput
        });
    }
}

// Property navigation (slider) functionality
function initializePropertyNavigation() {
    const prevButton = document.getElementById('prev-property');
    const nextButton = document.getElementById('next-property');
    const propertyGrid = document.getElementById('featured-properties');
    
    if (prevButton && nextButton && propertyGrid) {
        const propertyCards = propertyGrid.querySelectorAll('.property-card');
        const cardWidth = propertyCards.length > 0 ? propertyCards[0].offsetWidth + 20 : 0; // card width + gap
        let currentPosition = 0;
        let visibleCards = getVisibleCardCount();
        
        // Update the number of visible cards on window resize
        window.addEventListener('resize', function() {
            visibleCards = getVisibleCardCount();
            
            // Reset position if we're at the end but the window has grown
            if (currentPosition > propertyCards.length - visibleCards) {
                currentPosition = Math.max(0, propertyCards.length - visibleCards);
                updatePropertyGridPosition();
            }
        });
        
        // Previous button click
        prevButton.addEventListener('click', function() {
            if (currentPosition > 0) {
                currentPosition--;
                updatePropertyGridPosition();
            }
        });
        
        // Next button click
        nextButton.addEventListener('click', function() {
            if (currentPosition < propertyCards.length - visibleCards) {
                currentPosition++;
                updatePropertyGridPosition();
            }
        });
        
        // Helper function to update the grid position
        function updatePropertyGridPosition() {
            const translateX = -currentPosition * cardWidth;
            propertyGrid.style.transform = `translateX(${translateX}px)`;
            propertyGrid.style.transition = 'transform 0.3s ease';
            
            // Update button states
            prevButton.disabled = currentPosition === 0;
            nextButton.disabled = currentPosition >= propertyCards.length - visibleCards;
            
            // Update button appearance
            if (prevButton.disabled) {
                prevButton.classList.add('disabled');
            } else {
                prevButton.classList.remove('disabled');
            }
            
            if (nextButton.disabled) {
                nextButton.classList.add('disabled');
            } else {
                nextButton.classList.remove('disabled');
            }
        }
        
        // Calculate how many cards are visible based on container width
        function getVisibleCardCount() {
            const containerWidth = document.querySelector('.property-slider').offsetWidth;
            return Math.floor(containerWidth / cardWidth);
        }
        
        // Initialize grid styles
        propertyGrid.style.display = 'flex';
        propertyGrid.style.width = `${propertyCards.length * cardWidth}px`;
        propertyGrid.style.transition = 'transform 0.3s ease';
        
        // Initialize button states
        updatePropertyGridPosition();
    }
}

// Form validation
function initializeForms() {
    const registerForm = document.getElementById('register-form');
    const loginForm = document.getElementById('login-form');
    const propertyForm = document.getElementById('property-form');
    
    // Register form validation
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                showError('confirm-password', 'Passwords do not match');
            }
        });
    }
    
    // Login form validation
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            
            if (!email || !password) {
                e.preventDefault();
                showError('login-error', 'Please enter both email and password');
            }
        });
    }
    
    // Property form validation
    if (propertyForm) {
        propertyForm.addEventListener('submit', function(e) {
            const title = document.getElementById('title').value;
            const price = document.getElementById('price').value;
            const location = document.getElementById('location').value;
            
            if (!title || !price || !location) {
                e.preventDefault();
                showError('property-error', 'Please fill in all required fields');
            }
        });
    }
}

// Favorites functionality
function initializeFavorites() {
    const favoriteButtons = document.querySelectorAll('.favorite-button');
    
    favoriteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const propertyId = this.dataset.propertyId;
            const icon = this.querySelector('i');
            const isFavorite = icon.classList.contains('fas');
            
            // Send AJAX request to add/remove from favorites
            fetch(`favorite?propertyId=${propertyId}&action=${isFavorite ? 'remove' : 'add'}`, {
                method: 'POST',
                credentials: 'same-origin'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Toggle heart icon
                    icon.classList.toggle('fas');
                    icon.classList.toggle('far');
                    
                    if (icon.classList.contains('fas')) {
                        icon.style.color = '#ff4081';
                    } else {
                        icon.style.color = '';
                    }
                } else {
                    // If user is not logged in, redirect to login
                    if (data.redirect) {
                        window.location.href = data.redirect;
                    }
                }
            })
            .catch(error => {
                console.error('Error updating favorite:', error);
            });
        });
    });
}

// Helper function to show error messages
function showError(elementId, message) {
    const element = document.getElementById(elementId);
    const errorElement = document.createElement('div');
    errorElement.className = 'error-message';
    errorElement.textContent = message;
    
    // Remove any existing error messages
    const existingError = element.parentNode.querySelector('.error-message');
    if (existingError) {
        existingError.remove();
    }
    
    // Add the new error message
    element.parentNode.appendChild(errorElement);
    
    // Highlight the input field
    element.classList.add('error');
    
    // Remove the error after 3 seconds
    setTimeout(() => {
        errorElement.remove();
        element.classList.remove('error');
    }, 3000);
} 
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.BuyerDetails" %>
<%@ page import="com.bookstore.models.SavedCard" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    BuyerDetails buyerDetails = (BuyerDetails) session.getAttribute("buyerDetails");
    if (buyerDetails == null) {
        response.sendRedirect(request.getContextPath() + "/buyer/buyer-details");
        return;
    }
    
    @SuppressWarnings("unchecked")
    List<SavedCard> savedCards = (List<SavedCard>) request.getAttribute("savedCards");
    Double cartTotal = (Double) session.getAttribute("cartTotal");
    Double discount = (Double) session.getAttribute("discount");
    if (discount == null) discount = 0.0;
    double finalTotal = (cartTotal != null ? cartTotal : 0.0) - discount;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment - BookNest</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px 0;
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
        }
        .payment-method {
            border: 2px solid #dee2e6;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 15px;
            cursor: pointer;
            transition: all 0.3s;
        }
        .payment-method:hover {
            border-color: #667eea;
            background: #f8f9fa;
        }
        .payment-method.selected {
            border-color: #667eea;
            background: #e7f1ff;
        }
        .payment-method input[type="radio"] {
            width: 20px;
            height: 20px;
        }
        .card-display {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 10px;
            cursor: pointer;
        }
        
        .delete-card-btn {
            opacity: 0.7;
            transition: all 0.2s ease;
            background: rgba(220, 53, 69, 0.2);
            border: 1px solid rgba(220, 53, 69, 0.5);
            color: white;
        }
        
        .delete-card-btn:hover {
            opacity: 1;
            transform: scale(1.1);
            background: rgba(220, 53, 69, 0.8);
            border-color: #dc3545;
        }
        
        .delete-card-btn:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        .step-indicator {
            display: flex;
            justify-content: space-between;
            margin-bottom: 30px;
        }
        .step {
            flex: 1;
            text-align: center;
            position: relative;
        }
        .step::before {
            content: '';
            position: absolute;
            top: 25px;
            left: 50%;
            width: 100%;
            height: 2px;
            background: #dee2e6;
            z-index: -1;
        }
        .step:last-child::before {
            display: none;
        }
        .step-number {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #dee2e6;
            color: #6c757d;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            margin-bottom: 10px;
        }
        .step.active .step-number {
            background: #667eea;
            color: white;
        }
        .step.completed .step-number {
            background: #28a745;
            color: white;
        }
    </style>
</head>
<body>
    <div class="container" style="max-width: 1200px;">
        <!-- Progress Steps -->
        <div class="step-indicator mb-4">
            <div class="step completed">
                <div class="step-number"><i class="fas fa-check"></i></div>
                <div>Cart</div>
            </div>
            <div class="step completed">
                <div class="step-number"><i class="fas fa-check"></i></div>
                <div>Details</div>
            </div>
            <div class="step active">
                <div class="step-number">3</div>
                <div>Payment</div>
            </div>
            <div class="step">
                <div class="step-number">4</div>
                <div>Confirmation</div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-8">
                <div class="card mb-4">
                    <div class="card-body p-4">
                        <h4 class="mb-4">
                            <i class="fas fa-credit-card text-primary"></i> Select Payment Method
                        </h4>

                        <% if (request.getAttribute("errorMessage") != null) { %>
                            <div class="alert alert-danger alert-dismissible fade show">
                                <%= request.getAttribute("errorMessage") %>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        <% } %>

                        <form method="post" id="paymentForm" onsubmit="return validatePayment()">
                            <!-- Card Payment -->
                            <div class="payment-method" onclick="selectPaymentMethod('card')">
                                <div class="d-flex align-items-center mb-3">
                                    <input type="radio" name="paymentMethod" value="CARD" id="cardMethod">
                                    <label for="cardMethod" class="ms-3 mb-0 flex-grow-1">
                                        <h5 class="mb-0">
                                            <i class="fas fa-credit-card text-primary"></i> Credit/Debit Card
                                        </h5>
                                    </label>
                                </div>
                                
                                <div id="cardDetails" style="display:none;">
                                    <hr>
                                    
                                    <!-- Saved Cards -->
                                    <% if (savedCards != null && !savedCards.isEmpty()) { %>
                                        <h6 class="mb-3">Saved Cards</h6>
                                        <% for (SavedCard card : savedCards) { %>
                                            <div class="card-display" id="card-<%=card.getCardId()%>">
                                                <div class="d-flex justify-content-between align-items-center">
                                                    <div class="flex-grow-1" onclick="selectSavedCard('<%=card.getCardId()%>')" style="cursor: pointer;">
                                                        <div><i class="fas fa-credit-card"></i> <%=card.getCardType()%></div>
                                                        <div>**** **** **** <%=card.getCardNumber()%></div>
                                                        <small class="text-muted">Expires: <%=card.getExpiryDate()%></small>
                                                    </div>
                                                    <div class="d-flex align-items-center">
                                                        <input type="radio" name="savedCard" value="<%=card.getCardId()%>" class="me-2">
                                                        <button type="button" class="btn btn-outline-danger btn-sm delete-card-btn" 
                                                                onclick="deleteSavedCard('<%=card.getCardId()%>')" 
                                                                title="Delete this card">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        <% } %>
                                        <div class="text-center my-3">
                                            <button type="button" class="btn btn-outline-primary btn-sm" onclick="showNewCardForm()">
                                                <i class="fas fa-plus"></i> Add New Card
                                            </button>
                                        </div>
                                    <% } %>

                                    <!-- New Card Form -->
                                    <div id="newCardForm" style="display:<%= (savedCards == null || savedCards.isEmpty()) ? "block" : "none" %>;">
                                        <div class="row">
                                            <div class="col-md-12 mb-3">
                                                <label class="form-label">Card Holder Name</label>
                                                <input type="text" class="form-control" name="cardHolderName" id="cardHolderName">
                                            </div>
                                            <div class="col-md-12 mb-3">
                                                <label class="form-label">Card Number</label>
                                                <input type="text" class="form-control" name="cardNumber" id="cardNumber" 
                                                       maxlength="16" pattern="[0-9]{16}">
                                            </div>
                                            <div class="col-md-6 mb-3">
                                                <label class="form-label">Expiry Date (MM/YYYY)</label>
                                                <input type="text" class="form-control" name="expiryDate" id="expiryDate" 
                                                       placeholder="MM/YYYY" pattern="(0[1-9]|1[0-2])\/[0-9]{4}">
                                            </div>
                                            <div class="col-md-6 mb-3">
                                                <label class="form-label">CVV</label>
                                                <input type="text" class="form-control" name="cvv" id="cvv" 
                                                       maxlength="3" pattern="[0-9]{3}">
                                            </div>
                                            <div class="col-12">
                                                <div class="form-check">
                                                    <input type="checkbox" class="form-check-input" name="saveCard" id="saveCard">
                                                    <label class="form-check-label" for="saveCard">
                                                        Save this card for future purchases
                                                    </label>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- UPI Payment -->
                            <div class="payment-method" onclick="selectPaymentMethod('upi')">
                                <div class="d-flex align-items-center mb-3">
                                    <input type="radio" name="paymentMethod" value="UPI" id="upiMethod">
                                    <label for="upiMethod" class="ms-3 mb-0">
                                        <h5 class="mb-0">
                                            <i class="fas fa-mobile-alt text-success"></i> UPI / Online Wallet
                                        </h5>
                                    </label>
                                </div>
                                
                                <div id="upiDetails" style="display:none;">
                                    <hr>
                                    <div class="mb-3">
                                        <label class="form-label">UPI ID</label>
                                        <input type="text" class="form-control" name="upiId" id="upiId" 
                                               placeholder="yourname@upi">
                                    </div>
                                </div>
                            </div>

                            <!-- Cash on Delivery -->
                            <div class="payment-method" onclick="selectPaymentMethod('cod')">
                                <div class="d-flex align-items-center">
                                    <input type="radio" name="paymentMethod" value="COD" id="codMethod">
                                    <label for="codMethod" class="ms-3 mb-0">
                                        <h5 class="mb-0">
                                            <i class="fas fa-money-bill-wave text-warning"></i> Cash on Delivery
                                        </h5>
                                    </label>
                                </div>
                            </div>

                            <div class="d-flex justify-content-between mt-4">
                                <a href="${pageContext.request.contextPath}/buyer/buyer-details" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left"></i> Back
                                </a>
                                <button type="submit" class="btn btn-success btn-lg px-5">
                                    <i class="fas fa-check-circle"></i> Place Order ($<%=String.format("%.2f", finalTotal)%>)
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Order Summary -->
            <div class="col-lg-4">
                <div class="card sticky-top" style="top: 20px;">
                    <div class="card-body">
                        <h5 class="card-title mb-4">Order Summary</h5>
                        
                        <div class="mb-3">
                            <h6>Delivery Address:</h6>
                            <p class="text-muted small mb-0"><%=buyerDetails.getFullName()%></p>
                            <p class="text-muted small mb-0"><%=buyerDetails.getShippingAddress()%></p>
                            <p class="text-muted small mb-0"><%=buyerDetails.getCity()%>, <%=buyerDetails.getPostalCode()%></p>
                            <p class="text-muted small"><%=buyerDetails.getPhoneNumber()%></p>
                        </div>

                        <hr>
                        
                        <div class="d-flex justify-content-between mb-2">
                            <span>Subtotal</span>
                            <span>$<%=String.format("%.2f", cartTotal)%></span>
                        </div>
                        
                        <% if (discount > 0) { %>
                        <div class="d-flex justify-content-between mb-2 text-success">
                            <span><i class="fas fa-tag"></i> Discount</span>
                            <span>-$<%=String.format("%.2f", discount)%></span>
                        </div>
                        <% } %>
                        
                        <div class="d-flex justify-content-between mb-3">
                            <span>Shipping</span>
                            <span class="text-success">Free</span>
                        </div>
                        
                        <hr>
                        
                        <div class="d-flex justify-content-between">
                            <strong>Total Amount</strong>
                            <strong class="text-primary h4">$<%=String.format("%.2f", finalTotal)%></strong>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function selectPaymentMethod(method) {
            // Hide all detail sections
            document.getElementById('cardDetails').style.display = 'none';
            document.getElementById('upiDetails').style.display = 'none';
            
            // Remove selected class from all methods
            document.querySelectorAll('.payment-method').forEach(pm => pm.classList.remove('selected'));
            
            // Show selected method details
            if (method === 'card') {
                document.getElementById('cardMethod').checked = true;
                document.getElementById('cardDetails').style.display = 'block';
                document.getElementById('cardMethod').closest('.payment-method').classList.add('selected');
            } else if (method === 'upi') {
                document.getElementById('upiMethod').checked = true;
                document.getElementById('upiDetails').style.display = 'block';
                document.getElementById('upiMethod').closest('.payment-method').classList.add('selected');
            } else if (method === 'cod') {
                document.getElementById('codMethod').checked = true;
                document.getElementById('codMethod').closest('.payment-method').classList.add('selected');
            }
        }

        function selectSavedCard(cardId) {
            document.querySelector('input[name="savedCard"][value="' + cardId + '"]').checked = true;
            document.getElementById('newCardForm').style.display = 'none';
        }

        function showNewCardForm() {
            document.getElementById('newCardForm').style.display = 'block';
            document.querySelectorAll('input[name="savedCard"]').forEach(radio => radio.checked = false);
        }

        function deleteSavedCard(cardId) {
            if (confirm('Are you sure you want to delete this saved card? This action cannot be undone.')) {
                // Show loading state
                const deleteBtn = document.querySelector('button[onclick="deleteSavedCard(\'' + cardId + '\')"]');
                let originalContent = '';
                if (deleteBtn) {
                    originalContent = deleteBtn.innerHTML;
                    deleteBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
                    deleteBtn.disabled = true;
                }
                
                // Make AJAX request to delete the card
                fetch('${pageContext.request.contextPath}/buyer/delete-saved-card', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'cardId=' + encodeURIComponent(cardId)
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Remove the card from the UI
                        const cardElement = document.getElementById('card-' + cardId);
                        if (cardElement) {
                            cardElement.style.transition = 'opacity 0.3s ease';
                            cardElement.style.opacity = '0';
                            setTimeout(() => {
                                cardElement.remove();
                                
                                // Check if no saved cards remain
                                const remainingCards = document.querySelectorAll('.card-display');
                                if (remainingCards.length === 0) {
                                    // Hide the saved cards section and show "Add New Card" button
                                    const savedCardsSection = document.querySelector('.card-display').parentElement;
                                    if (savedCardsSection) {
                                        savedCardsSection.innerHTML = '<div class="text-center my-3"><button type="button" class="btn btn-outline-primary btn-sm" onclick="showNewCardForm()"><i class="fas fa-plus"></i> Add New Card</button></div>';
                                    }
                                }
                            }, 300);
                        }
                        
                        // Show success message
                        showNotification('Card deleted successfully', 'success');
                    } else {
                        // Show error message
                        showNotification(data.message || 'Failed to delete card', 'error');
                        // Restore button state
                        if (deleteBtn && originalContent) {
                            deleteBtn.innerHTML = originalContent;
                            deleteBtn.disabled = false;
                        }
                    }
                })
                .catch(error => {
                    console.error('Error deleting card:', error);
                    showNotification('An error occurred while deleting the card', 'error');
                    // Restore button state
                    if (deleteBtn && originalContent) {
                        deleteBtn.innerHTML = originalContent;
                        deleteBtn.disabled = false;
                    }
                });
            }
        }

        function showNotification(message, type) {
            // Create notification element
            const notification = document.createElement('div');
            const alertClass = (type === 'success') ? 'alert-success' : 'alert-danger';
            notification.className = 'alert ' + alertClass + ' alert-dismissible fade show position-fixed';
            notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
            notification.innerHTML = message + 
                '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
            
            document.body.appendChild(notification);
            
            // Auto-remove after 5 seconds
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                }
            }, 5000);
        }

        function validatePayment() {
            const selectedMethod = document.querySelector('input[name="paymentMethod"]:checked');
            
            if (!selectedMethod) {
                alert('Please select a payment method');
                return false;
            }

            if (selectedMethod.value === 'CARD') {
                const savedCard = document.querySelector('input[name="savedCard"]:checked');
                
                if (!savedCard && document.getElementById('newCardForm').style.display === 'block') {
                    // Validate new card details
                    const cardHolderName = document.getElementById('cardHolderName').value.trim();
                    const cardNumber = document.getElementById('cardNumber').value.trim();
                    const expiryDate = document.getElementById('expiryDate').value.trim();
                    const cvv = document.getElementById('cvv').value.trim();
                    
                    if (!cardHolderName || !cardNumber || !expiryDate || !cvv) {
                        alert('Please fill in all card details');
                        return false;
                    }
                    
                    if (!/^[0-9]{16}$/.test(cardNumber)) {
                        alert('Card number must be 16 digits');
                        return false;
                    }
                    
                    if (!/^(0[1-9]|1[0-2])\/[0-9]{4}$/.test(expiryDate)) {
                        alert('Invalid expiry date format (MM/YYYY)');
                        return false;
                    }
                    
                    if (!/^[0-9]{3}$/.test(cvv)) {
                        alert('CVV must be 3 digits');
                        return false;
                    }
                }
            } else if (selectedMethod.value === 'UPI') {
                const upiId = document.getElementById('upiId').value.trim();
                if (!upiId) {
                    alert('Please enter UPI ID');
                    return false;
                }
            }

            return true;
        }
    </script>
</body>
</html>

